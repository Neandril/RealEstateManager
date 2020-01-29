package com.neandril.realestatemanager.views.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.neandril.realestatemanager.R
import com.neandril.realestatemanager.models.Estate
import com.neandril.realestatemanager.utils.toSquare
import com.neandril.realestatemanager.utils.toThousand
import com.neandril.realestatemanager.viewmodels.EstateViewModel
import com.neandril.realestatemanager.views.base.BaseActivity

class CreateRealEstate : BaseActivity() {

    private var apikey: String = "AIzaSyCBXu4zBIgpguLv2RttqFF6xNE9s6sTgR4"
    private lateinit var placesClient: PlacesClient

    private lateinit var estateViewModel: EstateViewModel

    private lateinit var spinner: Spinner
    private lateinit var addresseTextView: TextView
    private lateinit var priceEditText: EditText
    private lateinit var surfaceEditText: EditText
    private lateinit var nbBathRoomsEditText: EditText
    private lateinit var nbBedRoomsEditText: EditText
    private lateinit var nbOtherRoomsEditText: EditText
    private lateinit var btnPhoto: ImageButton
    private lateinit var btnAddAddress: ImageButton

    companion object {
        const val AUTOCOMPLETE_REQUEST_CODE = 3010
    }

    override fun getActivityLayout(): Int {
        return R.layout.activity_create_real_estate
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Places.initialize(this, apikey)
        placesClient = Places.createClient(this)

        spinner = findViewById(R.id.spinner_estate)
        // addressEditText = findViewById(R.id.autocomplete_address)
        addresseTextView = findViewById(R.id.textView_address)
        priceEditText = findViewById(R.id.edittext_price)
        surfaceEditText = findViewById(R.id.edittext_surface)
        nbBathRoomsEditText = findViewById(R.id.edittext_nb_bathroom)
        nbBedRoomsEditText = findViewById(R.id.edittext_nb_bedroom)
        nbOtherRoomsEditText = findViewById(R.id.edittext_nb_other_rooms)
        btnPhoto = findViewById(R.id.btn_estate_photo)
        btnAddAddress = findViewById(R.id.btn_add_address)

        val button = findViewById<Button>(R.id.button_continue)
        button.setOnClickListener {
            // Example : Estate("Hello", "Apartment", "1.200.00", "150 m²", "3", "2", "5")
            val estate = Estate(
                //addressEditText.text.toString(),
                "",
                spinner.selectedItem.toString(),
                priceEditText.text.toString().toThousand(),
                surfaceEditText.text.toString().toSquare(),
                nbBathRoomsEditText.text.toString(),
                nbBedRoomsEditText.text.toString(),
                nbOtherRoomsEditText.text.toString()
            )

            estateViewModel = ViewModelProvider(this).get(EstateViewModel::class.java)
            estateViewModel.insert(estate)

/*            val replyIntent = Intent()
            if (TextUtils.isEmpty(addressEditText.text)) {
                setResult(Activity.RESULT_CANCELED, replyIntent)
            } else {

                val estate = addressEditText.text.toString()
                val price = priceEditText.text.toString()
                replyIntent.putExtra(EXTRA_REPLY, estate)
                replyIntent.putExtra(EXTRA_REPLY, price)
                setResult(Activity.RESULT_OK, replyIntent)
            }*/
            finish()
        }

        this.configureSpinner()
        this.buttonPhotoClick()
        this.buttonAddAddressClick()
    }


    private fun configureSpinner() {
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.estate_type, R.layout.support_simple_spinner_dropdown_item
        )
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
        spinner.adapter = adapter
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
        }

    }

    private fun buttonAddAddressClick() {
        btnAddAddress.setOnClickListener {
            val fields = listOf(
                Place.Field.ID,
                Place.Field.ADDRESS,
                Place.Field.ADDRESS_COMPONENTS
            )

            // Start the autocomplete intent.
            val intent = Autocomplete.IntentBuilder(
                AutocompleteActivityMode.FULLSCREEN, fields
            ).setCountry("US").setTypeFilter(TypeFilter.ADDRESS)
                .build(this)
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    val place = Autocomplete.getPlaceFromIntent(data!!)

                    Log.i(
                        "CreateRealEstate",
                        "address:" + place.address + "components:" + place.addressComponents
                    )
                    val address = place.address
                    // do query with address

                    addresseTextView.text = address
                }
                AutocompleteActivity.RESULT_ERROR -> {
                    // TODO: Handle the error.
                    val status = Autocomplete.getStatusFromIntent(data!!)
                    Toast.makeText(
                        this,
                        "Error: " + status.statusMessage!!,
                        Toast.LENGTH_LONG
                    ).show()
                    Log.i("CreateRealEstate", status.statusMessage!!)
                }
                Activity.RESULT_CANCELED -> {
                    // The user canceled the operation.
                }
            }

            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}
