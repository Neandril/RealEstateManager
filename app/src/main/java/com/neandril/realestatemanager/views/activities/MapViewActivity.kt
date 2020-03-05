package com.neandril.realestatemanager.views.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.net.toUri
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.neandril.realestatemanager.R
import com.neandril.realestatemanager.models.Estate
import com.neandril.realestatemanager.utils.toSquare
import com.neandril.realestatemanager.utils.toThousand
import com.neandril.realestatemanager.viewmodels.EstateViewModel
import com.neandril.realestatemanager.views.base.BaseActivity
import kotlinx.android.synthetic.main.custom_map_icon.view.*

class MapViewActivity : BaseActivity(), OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    private lateinit var estateViewModel: EstateViewModel

    private lateinit var toolbar: Toolbar
    private lateinit var mMap: GoogleMap
    private var estateList: MutableList<Estate> = mutableListOf()

    companion object {
        const val ESTATE_ID = "id_estate"
    }

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

        Log.d("MapView", "EtateList (2): " + estateList.size)
    }

    private fun getEstates() : MutableList<Estate> {
        estateViewModel = ViewModelProvider(this).get(EstateViewModel::class.java)
        estateViewModel.allEstates.observe(this, Observer { estates ->
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
                    markerOptions.position(latLng).title("Title").snippet("Snippet").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                } else {
                    markerOptions.position(latLng).title("Title").snippet("Snippet").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                }

                val marker = mMap.addMarker(markerOptions)
                marker.tag = it

            }
        })
        return estateList
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val fakePos = LatLng(40.765005, -73.980480)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(fakePos, 15f))

        getEstates()

        mMap.setInfoWindowAdapter(CustomInfoAdapter())
        mMap.setOnInfoWindowClickListener(this)
    }

    override fun onInfoWindowClick(marker: Marker?) {
        val estate: Estate? = marker?.tag as Estate?

        if (estate != null) {
            val intent = Intent(this, EstateDetailsActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.putExtra(ESTATE_ID, estate.id)
            this.startActivity(intent)
        } else {
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
        }

    }

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