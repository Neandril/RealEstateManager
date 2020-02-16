package com.neandril.realestatemanager.views.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.neandril.realestatemanager.R
import com.neandril.realestatemanager.views.base.BaseActivity
import com.neandril.realestatemanager.views.fragments.FragmentEstateDetails

class EstateDetailsActivity : BaseActivity() {

    private val newEstateActivityRequestCode = 1

    private lateinit var toolbar: Toolbar

    private var estateId: Int? = null


    // ***************************
    // BASE METHODS
    // ***************************

    override fun getActivityLayout(): Int {
        return R.layout.activity_estate_details
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_estate_details)

        toolbar = findViewById(R.id.toolbar_details)

        setSupportActionBar(toolbar)

        showFragment(FragmentEstateDetails())
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_details, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_edit -> launchCreateNewEstateActivity()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showFragment(fragment: Fragment) {
        // Retrieve estate
        estateId = intent.getIntExtra("id_estate",0)
        val fragmentManager = supportFragmentManager
        val bundle = Bundle()
        bundle.putInt("id_estate", estateId!!)
        fragment.arguments = bundle
        fragmentManager.beginTransaction()
            .replace(R.id.estate_details_content_frame, fragment)
            .commit()
    }

    private fun launchCreateNewEstateActivity() {
        val intent = Intent(this, CreateRealEstate::class.java)
        intent.putExtra("estateId", estateId)
        startActivity(intent)
        // startActivityForResult(intent, newEstateActivityRequestCode)
    }
}