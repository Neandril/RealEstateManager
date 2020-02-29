package com.neandril.realestatemanager.views.activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.neandril.realestatemanager.R
import com.neandril.realestatemanager.utils.Utils
import com.neandril.realestatemanager.views.base.BaseActivity
import com.neandril.realestatemanager.views.fragments.FragmentEstateDetails

class EstateDetailsActivity : BaseActivity() {

    private lateinit var toolbar: Toolbar

    private var estateId: Int? = null
    private var estatePrice: Int? = null

    companion object {
        const val ID_ESTATE = "id_estate"
        const val ESTATE_PRICE = "estate_price"
    }


    // ***************************
    // BASE METHODS
    // ***************************

    override fun getActivityLayout(): Int {
        return R.layout.activity_estate_details
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        estateId = intent.getIntExtra(ID_ESTATE,0)
        estatePrice = intent.getIntExtra(ESTATE_PRICE, 0)

        toolbar = findViewById(R.id.toolbar_details)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        showFragment(FragmentEstateDetails())
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_details, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_edit -> launchCreateNewEstateActivity()
            R.id.action_currency_conv -> showConverterDialog()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showFragment(fragment: Fragment) {
        // Retrieve estate
        val fragmentManager = supportFragmentManager
        val bundle = Bundle()
        bundle.putInt("id_estate", estateId!!)
        fragment.arguments = bundle
        fragmentManager.beginTransaction()
            .replace(R.id.estate_details_content_frame, fragment)
            .commit()
    }

    private fun launchCreateNewEstateActivity() {
        val intent = Intent(this, CreateRealEstateActivity::class.java)
        intent.putExtra("estateId", estateId)
        startActivity(intent)
        // startActivityForResult(intent, newEstateActivityRequestCode)
    }

    private fun showConverterDialog() {
        val viewGroup = findViewById<ViewGroup>(android.R.id.content)
        val builder = AlertDialog.Builder(this)


        val dialogView = LayoutInflater.from(this).inflate(R.layout.custom_dialog_currency_converter, viewGroup, false)
        val editTextDollars = dialogView.findViewById<EditText>(R.id.edittext_dollars)
        val editTextEuros = dialogView.findViewById<EditText>(R.id.edittext_euros)

        val converted = Utils.convertDollarToEuro(estatePrice!!).toString()
        editTextDollars.setText(estatePrice.toString())
        editTextEuros.setText(converted)

        editTextDollars.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) { }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (editTextDollars.hasFocus() && !editTextDollars.text.isNullOrEmpty()) {
                    editTextEuros.setText(
                        Utils.convertDollarToEuro(
                            s.toString().toInt()
                        ).toString())
                }

            }
        })

        editTextEuros.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) { }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (editTextEuros.hasFocus() && !editTextEuros.text.isNullOrEmpty()) {
                    editTextDollars.setText(
                        Utils.convertEuroToDollar(
                            s.toString().toInt()
                        ).toString())
                }
            }
        })

        builder.setView(dialogView)
        builder.setCancelable(false)
        builder.setPositiveButton(R.string.dialog_ok)
        { _, _ -> }
        builder.show()
    }
}