package com.neandril.realestatemanager.views.fragments

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.neandril.realestatemanager.R
import com.neandril.realestatemanager.views.base.BaseFragment

class FragmentMap : BaseFragment(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var mMapFragment: SupportMapFragment

    override fun getFragmentLayout(): Int {
        return R.layout.fragment_map
    }

    override fun configureFragment() {
        this.configureMap()
    }

    private fun configureMap() {

        mMapFragment = childFragmentManager.findFragmentById(R.id.map_view) as SupportMapFragment

        mMapFragment.getMapAsync(this)

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }
}