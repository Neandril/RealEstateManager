package com.neandril.realestatemanager.views.activities

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.neandril.realestatemanager.BuildConfig
import com.neandril.realestatemanager.R
import com.neandril.realestatemanager.models.Estate
import com.neandril.realestatemanager.models.Thumbnail
import com.neandril.realestatemanager.utils.toSquare
import com.neandril.realestatemanager.utils.toThousand
import com.neandril.realestatemanager.viewmodels.EstateViewModel
import com.neandril.realestatemanager.views.adapters.ImagesRecyclerViewAdapter
import com.neandril.realestatemanager.views.base.BaseActivity
import java.util.*
// OnMapAndViewReadyListener.OnGlobalLayoutAndMapReadyListener
class CreateRealEstate : BaseActivity(), OnMapReadyCallback {

    private var apikey: String = BuildConfig.ApiKey
    private lateinit var placesClient: PlacesClient

    private lateinit var estateViewModel: EstateViewModel

    private var image: Thumbnail = Thumbnail("","")
    private var imageDescription: String = ""
    private var strLatLng: String = ""
    private var latLng: LatLng? = null
    private var imgList: MutableList<Thumbnail> = mutableListOf()
    private var imageUri: Uri? = null

    private lateinit var sendButton: Button
    private lateinit var spinner: Spinner
    private lateinit var addressTextView: TextView
    private lateinit var priceEditText: EditText
    private lateinit var surfaceEditText: EditText
    private lateinit var nbBathRoomsEditText: EditText
    private lateinit var nbBedRoomsEditText: EditText
    private lateinit var nbOtherRoomsEditText: EditText
    private lateinit var btnPhoto: ImageButton
    private lateinit var btnAddAddress: ImageButton
    private lateinit var agentNameEditText: EditText
    private lateinit var cbStatus: CheckBox
    private lateinit var soldDateTextView: TextView
    private lateinit var descriptionEditText: EditText
    private lateinit var imageRecyclerView: RecyclerView

    companion object {
        const val AUTOCOMPLETE_REQUEST_CODE = 3010
        const val PERMISSION_CODE = 1001
        const val GALLERY = 100
        const val CAMERA = 101
    }

    private lateinit var mMap: GoogleMap

    // ***************************
    // BASE METHODS
    // ***************************

    override fun getActivityLayout(): Int {
        return R.layout.activity_create_real_estate
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Places.initialize(this, apikey)
        placesClient = Places.createClient(this)

        var mMapFragment = supportFragmentManager.findFragmentById(R.id.create_real_estate_map) as SupportMapFragment?

        if (mMapFragment == null) {
            mMapFragment = SupportMapFragment.newInstance()
            mMapFragment.getMapAsync(this)
        }

        // val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        // mapFragment?.let { OnMapAndViewReadyListener(it, this) }
        // mMapFragment?.getMapAsync(this)


        this.configureViews()
        this.configureSendButton()
        this.configureSpinner()
        this.configureSoldDate()
        this.buttonPhotoClick()
        this.buttonAddAddressClick()
    }

    // ***************************
    // UI SETUP
    // ***************************

    private fun configureSpinner() {
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.estate_type, R.layout.support_simple_spinner_dropdown_item
        )
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }

    private fun configureViews(){
        sendButton = findViewById(R.id.button_continue)
        spinner = findViewById(R.id.spinner_estate)
        addressTextView = findViewById(R.id.textView_address)
        priceEditText = findViewById(R.id.edittext_price)
        surfaceEditText = findViewById(R.id.edittext_surface)
        nbBathRoomsEditText = findViewById(R.id.edittext_nb_bathroom)
        nbBedRoomsEditText = findViewById(R.id.edittext_nb_bedroom)
        nbOtherRoomsEditText = findViewById(R.id.edittext_nb_other_rooms)
        btnPhoto = findViewById(R.id.btn_estate_photo)
        btnAddAddress = findViewById(R.id.btn_add_address)
        agentNameEditText = findViewById(R.id.agentName_editText)
        cbStatus = findViewById(R.id.cb_status)
        soldDateTextView = findViewById(R.id.textView_soldDate)
        descriptionEditText = findViewById(R.id.edittext_description)

    }

    private fun checkInputs(): Boolean {
        return !(addressTextView.text.isEmpty()  &&
                priceEditText.text.isEmpty() &&
                surfaceEditText.text.isEmpty() &&
                nbBathRoomsEditText.text.isEmpty() &&
                nbBedRoomsEditText.text.isEmpty() &&
                nbOtherRoomsEditText.text.isEmpty() &&
                agentNameEditText.text.isEmpty() &&
                descriptionEditText.text.isEmpty())
    }

    private fun configureSendButton() {

        sendButton.setOnClickListener {
            if (checkInputs()) {
                Log.d("CheckInput", "Form filled correctly")

                val estate = Estate(
                    0,
                    addressTextView.text.toString(),
                    strLatLng,
                    spinner.selectedItem.toString(),
                    priceEditText.text.toString().toThousand(),
                    surfaceEditText.text.toString().toSquare(),
                    nbBathRoomsEditText.text.toString(),
                    nbBedRoomsEditText.text.toString(),
                    nbOtherRoomsEditText.text.toString(),
                    agentNameEditText.text.toString(),
                    cbStatus.isChecked,
                    soldDateTextView.text.toString(),
                    descriptionEditText.text.toString(),
                    imgList
                )

                estateViewModel = ViewModelProvider(this).get(EstateViewModel::class.java)
                estateViewModel.insert(estate)
                finish()

            } else {
                Log.d("CheckInput", "Form not filled correctly")

                val builder = AlertDialog.Builder(this)
                builder.setTitle(R.string.dialog_inputs_title)
                builder.setMessage(R.string.dialog_verfy_inputs)
                builder.setPositiveButton(android.R.string.yes) { _, _ -> }
                builder.show()
            }
        }
    }

    // ***************************
    // PERMISSIONS
    // ***************************

    private fun checkPermissionsExternalStorage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_DENIED){
                // Permission denied
                val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                // Show popup to request runtime permission
                requestPermissions(permissions, PERMISSION_CODE)
            }
            else{
                // Permission already granted
                pickImageFromGallery()
            }
        }
        else{
            // System OS is < Marshmallow
            pickImageFromGallery()
        }
    }

    private fun checkPermissionsCamera() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (checkSelfPermission(Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED ||
                checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_DENIED){
                //permission was not enabled
                val permission = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                //show popup to request permission
                requestPermissions(permission, PERMISSION_CODE)
            }
            else{
                //permission already granted
                openCamera()
            }
        }
        else{
            //system os is < marshmallow
            openCamera()
        }
    }

    // ***************************
    // ACTIONS
    // ***************************

    private fun buttonAddAddressClick() {
        btnAddAddress.setOnClickListener {
            val fields = listOf(
                Place.Field.ID,
                Place.Field.ADDRESS,
                Place.Field.ADDRESS_COMPONENTS,
                Place.Field.LAT_LNG
            )

            // Start the autocomplete intent.
            val intent = Autocomplete.IntentBuilder(
                AutocompleteActivityMode.FULLSCREEN, fields
            ).setCountry("US").setTypeFilter(TypeFilter.ADDRESS)
                .build(this)
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
        }
    }

    private fun buttonPhotoClick() {
        btnPhoto.setOnClickListener {
            val viewGroup = findViewById<ViewGroup>(android.R.id.content)

            val dialogView = LayoutInflater.from(this).inflate(R.layout.custom_dialog_photo, viewGroup, false)
            val builder = AlertDialog.Builder(this)

            //setting the view of the builder to our custom view that we already inflated
            builder.setView(dialogView)

            //finally creating the alert dialog and displaying it
            val alertDialog = builder.create()
            alertDialog.show()

            val fromGallery = dialogView.findViewById<TextView>(R.id.from_gallery)
            fromGallery.setOnClickListener {
                Log.d("FromDialog", "From gallery clicked")
                checkPermissionsExternalStorage()
                alertDialog.dismiss()
            }

            val fromCamera = dialogView.findViewById<TextView>(R.id.from_camera)
            fromCamera.setOnClickListener {
                Log.d("FromDialog", "From camera clicked")
                checkPermissionsCamera()
                alertDialog.dismiss()
            }
        }
    }

    private fun showDescriptionDialog(path: String) {
        val viewGroup = findViewById<ViewGroup>(android.R.id.content)
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.dialog_room_desc_title)

        val dialogView = LayoutInflater.from(this).inflate(R.layout.custom_dialog_thumbnail_description, viewGroup, false)
        val descriptionEditText = dialogView.findViewById<EditText>(R.id.description_field)

        builder.setView(dialogView)
        builder.setPositiveButton(R.string.dialog_ok)
        { _, _ ->
            imageDescription = descriptionEditText.text.toString()
            image = Thumbnail(path, imageDescription)
            imgList.add(image)
            Log.d("CreateRealEstate", "Room: " + image.image + " , " + image.description)

            imageRecyclerView = findViewById(R.id.recyclerview_images)

            val adapter = ImagesRecyclerViewAdapter(this)
            imageRecyclerView.adapter = adapter
            imageRecyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
            adapter.setThumbnail(imgList)

            Log.d("List", "imagelist: $imgList")

        }

        builder.show()
    }

    private fun pickImageFromGallery() {
        //Intent to pick image
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, GALLERY)
    }

    private fun configureSoldDate() {
        cbStatus.setOnClickListener {
            if (cbStatus.isChecked) {
                soldDateTextView.visibility = View.VISIBLE
                this.configureDatePicker()
            } else {
                soldDateTextView.visibility = View.INVISIBLE
            }
        }
    }

    private fun configureDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        soldDateTextView.setOnClickListener {
            val datePickerDialog = DatePickerDialog(this, DatePickerDialog.OnDateSetListener
            { _, year, month, dayOfMonth ->
                Log.d("DatePicker", "Date: $year , $month , $dayOfMonth")
                soldDateTextView.text = getString(R.string.sold_date_format, year, month, dayOfMonth)
            }, year, month, day)
            datePickerDialog.show()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        Log.d("OnMapReady", "called")
        mMap = googleMap
        val brisbane = LatLng(-27.47093, 153.0235)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(brisbane, 10f))

/*        with (map) {
            uiSettings.isZoomControlsEnabled = false


            map.addMarker(MarkerOptions().position(brisbane).title("Brisbane"))
        }*/
    }

    private fun openCamera() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "New Picture")
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera")
        imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        //camera intent
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        startActivityForResult(cameraIntent, CAMERA)
    }

    // ***************************
    // RESULTS
    // ***************************

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            GALLERY -> {
                Log.d("CreateRealEstate", "requestCode: GALLERY")
                showDescriptionDialog(data?.data.toString())
                // imageRecyclerView.setImageURI(data?.data)
                // thumbnail.description = ""

                Log.d("CreateRealEstate", "Room: " + data?.data.toString())
            }
            CAMERA -> {
                Log.d("CreateRealEstate", "requestCode: CAMERA")
                showDescriptionDialog(imageUri.toString())
                // imageRecyclerView.setImageURI(data?.data)
                // thumbnail.description = ""

                Log.d("CreateRealEstate", "Room: ${imageUri.toString()}")

            }
            AUTOCOMPLETE_REQUEST_CODE -> {
                Log.d("CreateRealEstate", "requestCode: AUTOCOMPLETE")
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        val place = Autocomplete.getPlaceFromIntent(data!!)

                        val address = place.address
                        latLng = place.latLng

                        val strLat: String? = latLng?.latitude.toString()
                        val strLng: String? = latLng?.longitude.toString()
                        strLatLng = "$strLat,$strLng"

                        // do query with address
                        addressTextView.text = address

                        Log.d("CreateRealEstate", "address: $strLatLng")
                    }
                    AutocompleteActivity.RESULT_ERROR -> {
                        val status = Autocomplete.getStatusFromIntent(data!!)
                        Toast.makeText(this, "Error: " + status.statusMessage!!, Toast.LENGTH_LONG).show()
                        Log.i("CreateRealEstate", status.statusMessage!!)
                    }
                    Activity.RESULT_CANCELED -> {
                        // The user canceled the operation.
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        // mapFragment?.let { OnMapAndViewReadyListener(it, this) }
        mapFragment?.getMapAsync(this)
    }
}
