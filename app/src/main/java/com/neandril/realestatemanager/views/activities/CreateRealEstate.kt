package com.neandril.realestatemanager.views.activities

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.neandril.realestatemanager.R
import com.neandril.realestatemanager.views.base.BaseActivity

class CreateRealEstate : BaseActivity() {

    private lateinit var spinner: Spinner

    override fun getActivityLayout(): Int {
        return R.layout.activity_create_real_estate
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        spinner = findViewById(R.id.spinner_estate)

        title = "COLLAPSE"

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
