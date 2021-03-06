package com.neandril.realestatemanager.views.activities

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.lifecycle.Observer
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
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import com.neandril.realestatemanager.BuildConfig
import com.neandril.realestatemanager.R
import com.neandril.realestatemanager.models.Estate
import com.neandril.realestatemanager.models.Thumbnail
import com.neandril.realestatemanager.utils.Utils
import com.neandril.realestatemanager.utils.paddingZero
import com.neandril.realestatemanager.viewmodels.EstateViewModel
import com.neandril.realestatemanager.views.adapters.ImagesRecyclerViewAdapter
import com.neandril.realestatemanager.views.base.BaseActivity
import java.util.*

class CreateRealEstateActivity : BaseActivity(), OnMapReadyCallback {

    private var apikey: String = BuildConfig.ApiKey
    private lateinit var estateViewModel: EstateViewModel
    private lateinit var toolbar: Toolbar

    // Variables
    private var image: Thumbnail = Thumbnail("","")
    private var imageDescription: String = ""
    private var strLatLng: String = ""
    private var latLng: LatLng? = null
    private var imgList: MutableList<Thumbnail>? = mutableListOf()
    private var imageUri: Uri? = null
    private var estateId: Int = 0
    private var mSnapshot: Bitmap? = null

    private  lateinit var listItems:Array<String>
    private lateinit var checkedItems: BooleanArray
    private var mUserItem = arrayListOf<Int>()

    // Ui elements
    private lateinit var sendButton: Button
    private lateinit var spinner: Spinner
    private lateinit var addressTextView: TextView
    private lateinit var priceEditText: EditText
    private lateinit var surfaceEditText: EditText
    private lateinit var nbBathRoomsEditText: EditText
    private lateinit var nbBedRoomsEditText: EditText
    private lateinit var nbOtherRoomsEditText: EditText
    private lateinit var btnPhoto: ImageButton
    private lateinit var agentNameEditText: EditText
    private lateinit var cbStatus: CheckBox
    private lateinit var soldDateTextView: TextView
    private lateinit var descriptionEditText: EditText
    private lateinit var imageRecyclerView: RecyclerView
    private lateinit var cardviewMap: CardView
    private lateinit var mMap: GoogleMap
    private lateinit var poisTextView: TextView

    companion object {
        const val AUTOCOMPLETE_REQUEST_CODE = 3010
        const val GALLERY = 100
        const val CAMERA = 101
    }

    // ***************************
    // BASE METHODS
    // ***************************

    override fun getActivityLayout(): Int {
        return R.layout.activity_create_real_estate
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        toolbar = findViewById(R.id.toolbar_common)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        // Get the estateId from previous activity
        estateId = intent.getIntExtra("estateId", 0)
        if (estateId != 0) { editEstate(estateId) }

        this.configureViews()
        this.configureSendButton()
        this.configureSpinner()
        this.configureSoldDate()
        this.buttonPhotoClick()
        this.addressOnClick()
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
        nbOtherRoomsEditText = findViewById(R.id.edittext_nb_rooms)
        btnPhoto = findViewById(R.id.btn_estate_photo)
        agentNameEditText = findViewById(R.id.agentName_editText)
        cbStatus = findViewById(R.id.cb_status)
        soldDateTextView = findViewById(R.id.textView_soldDate)
        descriptionEditText = findViewById(R.id.edittext_description)
        cardviewMap = findViewById(R.id.cardview_map)
        poisTextView = findViewById(R.id.textview_pois)

        poisTextView.setOnClickListener {
            showPoiDialog()
        }
    }

    private fun checkInputs(): Boolean {
        if (imgList?.size == 0) {
            imgList = null
        }

        return !(addressTextView.text.isEmpty()  &&
                priceEditText.text.isEmpty() &&
                surfaceEditText.text.isEmpty() &&
                nbBathRoomsEditText.text.isEmpty() &&
                nbBedRoomsEditText.text.isEmpty() &&
                nbOtherRoomsEditText.text.isEmpty() &&
                agentNameEditText.text.isEmpty() &&
                descriptionEditText.text.isEmpty())
    }

    // ***************************
    // ACTIONS
    // ***************************

    private fun configureSendButton() {
        sendButton.setOnClickListener {
            if (checkInputs()) {
                val estate = Estate(
                    estateId,
                    addressTextView.text.toString(),
                    strLatLng,
                    spinner.selectedItem.toString(),
                    priceEditText.text.toString().toInt(),
                    surfaceEditText.text.toString(),
                    nbBathRoomsEditText.text.toString(),
                    nbBedRoomsEditText.text.toString(),
                    nbOtherRoomsEditText.text.toString(),
                    agentNameEditText.text.toString(),
                    cbStatus.isChecked,
                    soldDateTextView.text.toString(),
                    descriptionEditText.text.toString(),
                    imgList,
                    mSnapshot,
                    poisTextView.text.toString()
                )

                // If estateId == 0, create new one
                if (estateId == 0) {
                    estateViewModel = ViewModelProvider(this).get(EstateViewModel::class.java)
                    estateViewModel.insert(estate)
                } else { // Else, update current estate
                    estateViewModel = ViewModelProvider(this).get(EstateViewModel::class.java)
                    estateViewModel.updateEstate(estate)
                }

                finish()
            } else {
                val builder = AlertDialog.Builder(this)
                builder.setTitle(R.string.dialog_inputs_title)
                builder.setMessage(R.string.dialog_verfy_inputs)
                builder.setPositiveButton(android.R.string.yes) { _, _ -> }
                builder.show()
            }
        }
    }

    private fun editEstate(estateId: Int) {
        estateViewModel = ViewModelProvider(this).get(EstateViewModel::class.java)
        estateViewModel.getSingleEstate(estateId).observe(this, Observer { id ->
            priceEditText.setText(id.price.toString())
            surfaceEditText.setText(id.surface)
            nbBathRoomsEditText.setText(id.nbBathrooms)
            nbBedRoomsEditText.setText(id.nbBedrooms)
            nbOtherRoomsEditText.setText(id.nbTotalRooms)
            addressTextView.text = id.address
            descriptionEditText.setText(id.description)
            strLatLng = id.addressLatLng
            cardviewMap.visibility = View.VISIBLE
            agentNameEditText.setText(id.agentName)
            cbStatus.isChecked = id.sold
            soldDateTextView.text = id.soldDate
            poisTextView.text = id.points_of_interest

            // Get LatLng if available, else hide the map
            when {
                id.addressLatLng.isNotEmpty() -> {
                    // Convert srting to lat lng
                    val sLatLng = id.addressLatLng.split(",")
                    if (sLatLng.isNotEmpty()) {
                        val lat: Double = sLatLng[0].toDouble()
                        val lng: Double = sLatLng[1].toDouble()
                        latLng = LatLng(lat, lng)
                    }

                    val mMapFragment = this.supportFragmentManager.findFragmentById(R.id.create_real_estate_map) as SupportMapFragment?
                    mMapFragment?.getMapAsync(this)
                }
                else -> cardviewMap.visibility = View.GONE
            }

            // Set up image list
            if (imgList != null) {
                id.estatePhotos?.forEach {
                    imgList?.add(it)
                }

                imageRecyclerView = findViewById(R.id.recyclerview_images)

                val adapter = ImagesRecyclerViewAdapter(this)
                imageRecyclerView.adapter = adapter
                imageRecyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
                adapter.setThumbnail(this.imgList!!)
            }
        })
    }

    private fun addressOnClick() {
        addressTextView.setOnClickListener {
            if (Utils.isInternetAvailable(this)) {
                // Initialize map api
                Places.initialize(this, apikey)

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
            } else {
                Toast.makeText(this, R.string.not_connected, Toast.LENGTH_LONG).show()
            }
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
                checkPermissionsExternalStorage()
                alertDialog.dismiss()
            }

            val fromCamera = dialogView.findViewById<TextView>(R.id.from_camera)
            fromCamera.setOnClickListener {
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
            imgList?.add(image)

            imageRecyclerView = findViewById(R.id.recyclerview_images)

            val adapter = ImagesRecyclerViewAdapter(this)
            imageRecyclerView.adapter = adapter
            imageRecyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
            adapter.setThumbnail(this.imgList!!)
        }

        builder.show()
    }

    private fun showPoiDialog() {
        listItems = resources.getStringArray(R.array.point_of_interest)
        checkedItems = BooleanArray(listItems.size)

        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.poi_title)

        builder.setMultiChoiceItems(listItems, checkedItems
        ) { _, position, isChecked ->
            if (isChecked) {
                mUserItem.add(position)
            } else {
                mUserItem.remove((Integer.valueOf(position)))
            }
        }
        builder.setCancelable(false)
        builder.setPositiveButton(R.string.dialog_ok
        ) { _, _ ->
            var item = ""
            for (i in 0 until mUserItem.size) {
                item += listItems[mUserItem[i]]
                if (i != mUserItem.size - 1) {
                    item = "$item, "
                }
            }
            poisTextView.text = item
        }

        val mDialog = builder.create()
        mDialog.show()
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
                soldDateTextView.text = null
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
                soldDateTextView.text = getString(R.string.sold_date_format, year, month.paddingZero(), dayOfMonth.paddingZero())
            }, year, month, day)
            datePickerDialog.show()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        Log.d("OnMapReady", "called")
        mMap = googleMap
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))

        val markerOptions = latLng?.let { MarkerOptions().position(it) }
        mMap.addMarker(markerOptions)

        // When map is ready, take a snapshot
        mMap.setOnMapLoadedCallback {
            createSnapshot()
        }
        createSnapshot()
    }

    private fun createSnapshot() {
        val callback: GoogleMap.SnapshotReadyCallback =
            GoogleMap.SnapshotReadyCallback { snapshot ->
                // Callback is called from the main thread, so we can modify the ImageView safely.
                mSnapshot = snapshot
            }

        mMap.snapshot(callback, mSnapshot)
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
    // PERMISSIONS
    // ***************************

    private fun checkPermissionsExternalStorage() {
        Dexter
            .withActivity(this)
            .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
            .withListener(object: PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                    pickImageFromGallery()
                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest?,
                    token: PermissionToken?
                ) {
                    Log.d("Dexter", "Permission Rationale")
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                    Log.d("Dexter", "Permission Denied")
                }

            })
            .check()
    }

    private fun checkPermissionsCamera() {
        Dexter
            .withActivity(this)
            .withPermissions(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .withListener(object: MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    openCamera()
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    Log.d("Dexter", "Permission Rationale")
                }

            })
            .check()
    }

    // ***************************
    // RESULTS
    // ***************************

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            GALLERY -> {
                showDescriptionDialog(data?.data.toString())
                // imageRecyclerView.setImageURI(data?.data)
                // thumbnail.description = ""
            }
            CAMERA -> {
                showDescriptionDialog(imageUri.toString())
                // imageRecyclerView.setImageURI(data?.data)
                // thumbnail.description = ""
            }
            AUTOCOMPLETE_REQUEST_CODE -> {
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

                        cardviewMap.visibility = View.VISIBLE

                        val mMapFragment = this.supportFragmentManager.findFragmentById(R.id.create_real_estate_map) as SupportMapFragment?
                        mMapFragment?.getMapAsync(this)

                    }
                    AutocompleteActivity.RESULT_ERROR -> {
                        val status = Autocomplete.getStatusFromIntent(data!!)
                        Toast.makeText(this, "Error: " + status.statusMessage!!, Toast.LENGTH_LONG).show()
                    }
                    Activity.RESULT_CANCELED -> {
                        // The user canceled the operation.
                    }
                }
            }
        }
    }
}
