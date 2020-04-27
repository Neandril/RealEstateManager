package com.neandril.realestatemanager.views.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.net.toUri
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.neandril.realestatemanager.R
import com.neandril.realestatemanager.models.Estate
import com.neandril.realestatemanager.utils.toSquare
import com.neandril.realestatemanager.utils.toThousand
import com.neandril.realestatemanager.viewmodels.EstateViewModel
import com.neandril.realestatemanager.views.base.BaseFragment
import kotlinx.android.synthetic.main.custom_map_icon.view.*


class MapViewFragment : BaseFragment(), OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    // ***************************
    // DECLARATIONS
    // ***************************
    private lateinit var estateViewModel: EstateViewModel
    private var estateList: MutableList<Estate> = mutableListOf()

    private lateinit var mMap: GoogleMap
    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private var position: String = ""

    companion object {
        const val ESTATE_ID = "id_estate"
    }

    // ***************************
    // BASE METHODS
    // ***************************
    override fun getFragmentLayout(): Int {
        return R.layout.fragment_mapview
    }

    override fun configureFragment() {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this.activity!!)

        val mMapFragment = this.childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mMapFragment.getMapAsync(this)
    }

    override fun onResume() {
        super.onResume()
        getPermissions()
    }

    // ***************************
    // REQUESTS
    // ***************************
    private fun getEstates() : MutableList<Estate> {
        estateViewModel = ViewModelProvider(this).get(EstateViewModel::class.java)
        estateViewModel.getEstates().observe(this, Observer { estates ->
            estates.forEach {
                Log.d("MapView", "LatLng: " + it.addressLatLng)
                estateList.add(it)

                // Convert string to LatLng
                val latlong = it.addressLatLng.split(",")
                val latitude = latlong[0].toDouble()
                val longitude = latlong[1].toDouble()
                val latLng = LatLng(latitude, longitude)

                val markerOptions = MarkerOptions()
                if (it.sold) {
                    markerOptions.position(latLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                } else {
                    markerOptions.position(latLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                }

                val marker = mMap.addMarker(markerOptions)
                marker.tag = it

            }
        })

        estateViewModel.init()
        return estateList
    }

    // ***************************
    // MAP
    // ***************************
    override fun onMapReady(googleMap: GoogleMap) {
        Log.d("MapViewFragment", "onMapReady: ")
        mMap = googleMap
        mMap.uiSettings.isCompassEnabled = false
        mMap.uiSettings.isMyLocationButtonEnabled = true
        mMap.isMyLocationEnabled = true

        getEstates()

        mMap.setInfoWindowAdapter(CustomInfoAdapter())
        mMap.setOnInfoWindowClickListener(this)
    }

    override fun onInfoWindowClick(marker: Marker?) {
        val estate: Estate? = marker?.tag as Estate?

        if (estate != null) {
            val intent = Intent(activity, EstateDetailsActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.putExtra(ESTATE_ID, estate.id)
            this.startActivity(intent)
        } else {
            Toast.makeText(activity, "Error", Toast.LENGTH_SHORT).show()
        }
    }

    // ***************************
    // PERMISSIONS
    // ***************************
    private fun getPermissions() {
        Dexter
            .withActivity(activity)
            .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                    getLastKnownLocation()
                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest?,
                    token: PermissionToken?
                ) {
                    Log.e("Dexter", "Permission Rationale")
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                    Log.e("Dexter", "Permission Denied")
                }
            })
            .check()
    }

    // ***************************
    // POSITION
    // ***************************
    private fun getLastKnownLocation() {
        mFusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                latitude = location.latitude
                longitude = location.longitude
                val latLng = LatLng(latitude, longitude)
                position = "$latitude,$longitude"

                Log.d("MapViewFragment","Position: $position")

                cameraUpdate(latLng)
            } else {
                Toast.makeText(activity,"Error retrieving location", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun cameraUpdate(latLng: LatLng) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
    }

    // ***************************
    // ADAPTER FOR CUSTOM INFO
    // ***************************
    internal inner class CustomInfoAdapter : GoogleMap.InfoWindowAdapter {

        @SuppressLint("InflateParams")
        override fun getInfoContents(marker: Marker?): View {
            val mInfoView = layoutInflater.inflate(R.layout.custom_map_icon, null)

            val estate: Estate? = marker?.tag as Estate?

            if (estate?.estatePhotos != null) {
                val uri = estate.estatePhotos[0].image.toUri()

                Glide.with(mInfoView)
                    .load(uri)
                    .apply(RequestOptions.centerCropTransform())
                    .placeholder(R.drawable.no_preview_available)
                    .listener(object: RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            dataSource.let {
                                if (dataSource != DataSource.MEMORY_CACHE) {
                                    marker?.showInfoWindow()
                                }
                            }
                            return false
                        }

                    })
                    .into(mInfoView.map_estate_marker_picture)
            }

            mInfoView.map_estate_marker_title.text = estate?.type
            mInfoView.map_estate_marker_price.text = getString(R.string.custom_map_price_and_surface,
                estate?.price.toString().toThousand(),
                estate?.surface.toString().toSquare())

            return mInfoView
        }

        override fun getInfoWindow(marker: Marker?): View? {
            return null
        }
    }
}