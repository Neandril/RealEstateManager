package com.neandril.realestatemanager.views.activities

import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.neandril.realestatemanager.R
import com.neandril.realestatemanager.views.base.BaseActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.LatLng


class MapViewActivity : BaseActivity(), OnMapReadyCallback {

    private lateinit var toolbar: Toolbar
    private lateinit var mMap: GoogleMap

    override fun getActivityLayout(): Int {
        return R.layout.activity_map_view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        toolbar = findViewById(R.id.toolbar_map_view)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        val mMapFragment = this.supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mMapFragment?.getMapAsync(this)

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val sydney = LatLng(-33.852, 151.211)
        mMap.addMarker(
            MarkerOptions().position(sydney)
                .title("Marker in Sydney")
        )
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))

    }
}