package com.neandril.realestatemanager.views.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.neandril.realestatemanager.R
import com.neandril.realestatemanager.utils.Utils
import com.neandril.realestatemanager.views.base.BaseActivity

class MapViewActivity : BaseActivity() {

    private lateinit var toolbar: Toolbar

    override fun getActivityLayout(): Int {
        return R.layout.activity_map_view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        toolbar = findViewById(R.id.toolbar_details)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        if (Utils.isInternetAvailable(this)){
            val fragmentManager = supportFragmentManager
            fragmentManager.beginTransaction()
                .replace(R.id.activity_map_content_frame, MapViewFragment())
                .commit()
        } else {
            Toast.makeText(this, R.string.not_connected, Toast.LENGTH_LONG).show()
            finish()
        }

    }

}
