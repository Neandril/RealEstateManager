package com.neandril.realestatemanager.views.fragments


import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.neandril.realestatemanager.R
import com.neandril.realestatemanager.models.FilterModel
import com.neandril.realestatemanager.viewmodels.EstateViewModel
import com.neandril.realestatemanager.viewmodels.FilterInteractionViewModel
import com.neandril.realestatemanager.views.adapters.MainRecyclerViewAdapter
import com.neandril.realestatemanager.views.base.BaseFragment

class FragmentMain : BaseFragment() {

    private lateinit var estateViewModel: EstateViewModel
    private val filterInteraction: FilterInteractionViewModel by activityViewModels()

    // ***************************
    // BASE METHODS
    // ***************************
    
    override fun getFragmentLayout(): Int {
        return R.layout.fragment_main
    }

    override fun configureFragment() {
        estateViewModel = ViewModelProvider(this).get(EstateViewModel::class.java)

        //this.configureRecyclerView()
    }

    private fun configureRecyclerView() {
        val recyclerView = activity?.findViewById<RecyclerView>(R.id.recyclerview)
        val adapter = MainRecyclerViewAdapter(activity?.applicationContext!!)
        recyclerView?.adapter = adapter
        recyclerView?.layoutManager = LinearLayoutManager(activity?.applicationContext!!)

        estateViewModel.getEstates().observe(this, Observer { estates ->
            estates.let { adapter.setEstate(it) }
        })

        estateViewModel.init()
    }

    override fun onResume() {
        super.onResume()
        configureRecyclerView()

        filterInteraction.observeFilter().observe(this, Observer {
            if (it != null) {
                buildRequest(it)  
            } else {
                estateViewModel.init()
            }
        })
    }

    private fun buildRequest(filter: FilterModel) {
        val type = filter.estateType?.joinToString(",","", "") ?: ""
        val pointsOfInterest = filter.estatePois?.joinToString(",","", "") ?: ""

        estateViewModel.getFiltered(filter.minPrice, filter.maxPrice, filter.minSurface, filter.maxSurface,
            filter.nbRooms?: 0, type, pointsOfInterest, filter.location?: "",
            filter.isSold, filter.displayOnlyPhotos)
    }
}
