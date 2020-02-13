package com.neandril.realestatemanager.views.activities

import android.os.Bundle
import android.view.Menu
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.neandril.realestatemanager.R
import com.neandril.realestatemanager.views.base.BaseActivity
import com.neandril.realestatemanager.views.fragments.FragmentEstateDetails

class EstateDetailsActivity : BaseActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var text1: TextView

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

    private fun showFragment(fragment: Fragment) {
        // Retrieve estate
        val estateId = intent.getIntExtra("id_estate",0)
        val fragmentManager = supportFragmentManager
        val bundle = Bundle()
        bundle.putInt("id_estate", estateId)
        fragment.arguments = bundle
        fragmentManager.beginTransaction()
            .replace(R.id.estate_details_content_frame, fragment)
            .commit()
    }
}