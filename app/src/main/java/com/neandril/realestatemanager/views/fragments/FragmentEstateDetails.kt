package com.neandril.realestatemanager.views.fragments

import android.graphics.Color
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.neandril.realestatemanager.R
import com.neandril.realestatemanager.models.Estate
import com.neandril.realestatemanager.models.Thumbnail
import com.neandril.realestatemanager.utils.toSquare
import com.neandril.realestatemanager.utils.toThousand
import com.neandril.realestatemanager.viewmodels.EstateViewModel
import com.neandril.realestatemanager.views.adapters.DotIndicatorPagerAdapter
import com.neandril.realestatemanager.views.adapters.ZoomOutPageTransformer
import com.neandril.realestatemanager.views.base.BaseFragment
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator

class FragmentEstateDetails : BaseFragment() {

    private var typeTextView: TextView? = null
    private var priceTextView: TextView? = null
    private var surfaceTextView: TextView? = null
    private var descriptionTextView: TextView? = null
    private var nbBathRoomsTextView: TextView? = null
    private var nbBedRoomsTextView: TextView? = null
    private var nbOtherRoomsTextView: TextView? = null
    private var addressTextView: TextView? = null
    private var agentNameTextView: TextView? = null
    private var soldDateTextView: TextView? = null
    private var mapThumbnail: ImageView? = null
    private var statusTextView: TextView? = null
    private var viewpager: ViewPager? = null
    private var dotsIndicator: DotsIndicator? = null
    private var imgList: List<Thumbnail>? = listOf()

    private lateinit var estateViewModel: EstateViewModel

    override fun getFragmentLayout(): Int {
        return R.layout.fragment_estate_details
    }

    override fun configureFragment() {
        this.configureViews()

        val estateId = this.arguments?.getInt("id_estate") as Int
        estateViewModel = ViewModelProvider(this).get(EstateViewModel::class.java)
        estateViewModel.getSingleEstate(estateId).observe(this, Observer {estate ->
            displayDetails(estate)
        })
    }

    private fun configureViews() {
        typeTextView = activity?.findViewById(R.id.textView_details_type)
        priceTextView = activity?.findViewById(R.id.textView_details_price)
        surfaceTextView = activity?.findViewById(R.id.textView_details_surface)
        descriptionTextView = activity?.findViewById(R.id.textView_details_description)
        nbBathRoomsTextView = activity?.findViewById(R.id.textView_details_nbBathrooms)
        nbBedRoomsTextView = activity?.findViewById(R.id.textView_details_nbBedrooms)
        nbOtherRoomsTextView = activity?.findViewById(R.id.textView_details_nbOthers)
        addressTextView = activity?.findViewById(R.id.textView_details_address)
        agentNameTextView = activity?.findViewById(R.id.textView_details_agent)
        soldDateTextView = activity?.findViewById(R.id.textView_details_soldDate)
        statusTextView = activity?.findViewById(R.id.textView_details_status)
        mapThumbnail = activity?.findViewById(R.id.map_thumbnail)
        viewpager = activity?.findViewById(R.id.view_pager)
        dotsIndicator = activity?.findViewById(R.id.dots_indicator)
    }

    private fun displayDetails(estate: Estate) {
        // Split address after commas, and display it in a multilined style inside the textview
        val items = estate.address.split(", ")
        addressTextView?.text = estate.address.replace(", ", "• ")


        // Display the type, and city
        typeTextView?.text = getString(R.string.type_and_city, estate.type, items[1])

        // Fill all others textviews
        priceTextView?.text = estate.price.toThousand()
        surfaceTextView?.text = estate.surface.toSquare()
        nbBathRoomsTextView?.text = estate.nbBathrooms
        nbBedRoomsTextView?.text = estate.nbBedrooms
        nbOtherRoomsTextView?.text = estate.nbOtherRooms
        agentNameTextView?.text = estate.agentName
        imgList = estate.estatePhotos

        // Get the map thumbnail
        mapThumbnail?.setImageBitmap(estate.addressThumbnail)

        // Configure the viewpager
        with(viewpager) {
            this?.adapter = DotIndicatorPagerAdapter(imgList)
            this?.setPageTransformer(true, ZoomOutPageTransformer())

            this?.let { dotsIndicator?.setViewPager(viewPager = it) }
        }

        // Display the description
        when {
            estate.description.isEmpty() -> descriptionTextView?.text = getString(R.string.no_description)
            else -> descriptionTextView?.text = estate.description
        }

        when {
            estate.sold -> {
                statusTextView?.text = getString(R.string.has_been_sold)
                statusTextView?.setTextColor(Color.RED)
                soldDateTextView?.text = estate.soldDate
            }
            else -> {
                statusTextView?.text = getString(R.string.still_available)
                statusTextView?.setTextColor(Color.GREEN)
                soldDateTextView?.text = estate.soldDate
            }
        }
    }
}
