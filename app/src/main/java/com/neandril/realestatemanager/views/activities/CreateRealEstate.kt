package com.neandril.realestatemanager.views.activities

import android.app.Activity
import android.content.ClipData.Item
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.lifecycle.ViewModelProvider
import com.neandril.realestatemanager.R
import com.neandril.realestatemanager.models.Estate
import com.neandril.realestatemanager.viewmodels.EstateViewModel
import com.neandril.realestatemanager.views.base.BaseActivity

class CreateRealEstate : BaseActivity() {

    private lateinit var estateViewModel: EstateViewModel

    private lateinit var spinner: Spinner
    private lateinit var addressEditText: EditText
    private lateinit var priceEditText: EditText

    companion object {
        const val EXTRA_REPLY = "com.neandril.realestatemanager.estateListSql.REPLY"
    }

    override fun getActivityLayout(): Int {
        return R.layout.activity_create_real_estate
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        spinner = findViewById(R.id.spinner_estate)
        addressEditText = findViewById(R.id.edittext_address)
        priceEditText = findViewById(R.id.edittext_price)

        val button = findViewById<Button>(R.id.button_continue)
        button.setOnClickListener {

            val estate = Estate(
                addressEditText.text.toString(),
                priceEditText.text.toString()
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
    }

    fun configureSpinner() {
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.estate_type, R.layout.support_simple_spinner_dropdown_item
        )
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }

}
