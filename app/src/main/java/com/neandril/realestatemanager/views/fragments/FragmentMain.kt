package com.neandril.realestatemanager.views.fragments


import android.util.Log
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.neandril.realestatemanager.R
import com.neandril.realestatemanager.models.Estate
import com.neandril.realestatemanager.models.FilterModel
import com.neandril.realestatemanager.viewmodels.EstateViewModel
import com.neandril.realestatemanager.viewmodels.FilterInteractionViewModel
import com.neandril.realestatemanager.views.adapters.MainRecyclerViewAdapter
import com.neandril.realestatemanager.views.base.BaseFragment

class FragmentMain : BaseFragment() {

    private val estateViewModel: EstateViewModel by viewModels()
    private val filterInteraction: FilterInteractionViewModel by activityViewModels()

    // ***************************
    // BASE METHODS
    // ***************************
    
    override fun getFragmentLayout(): Int {
        return R.layout.fragment_main
    }

    override fun configureFragment() {
        //this.configureRecyclerView()
    }

    private fun configureRecyclerView() {
        val recyclerView = activity?.findViewById<RecyclerView>(R.id.recyclerview)
        val adapter = MainRecyclerViewAdapter(activity?.applicationContext!!)
        recyclerView?.adapter = adapter
        recyclerView?.layoutManager = LinearLayoutManager(activity?.applicationContext!!)

        estateViewModel.allEstates.observe(this, Observer { estates ->
            estates.let { adapter.setEstate(it) }
        })
    }

    override fun onResume() {
        super.onResume()

        filterInteraction.observeFilter().observe(this, Observer {
            buildRequest(it)
        })

/*        val filter = bundle?.getSerializable("FILTER") as FilterModel
        Log.d("onResume", "Filter: " + filter.minSurface + " - " + filter.maxSurface)*/

        if (estateViewModel.allEstates.hasObservers()) {
            Log.d("onResume", "allEstate: true")
        } else {
            Log.d("onResume", "allEstate: false")
        }
        configureRecyclerView()
    }

    private fun buildRequest(filter: FilterModel) {
        val type = filter.estateType?.joinToString(",","", "") ?: ""
        val pointsOfInterest = filter.estatePois?.joinToString(",","", "") ?: ""

        estateViewModel.getFiltered(filter.minPrice, filter.maxPrice, filter.minSurface, filter.maxSurface,
            filter.nbRooms?: 0, type, pointsOfInterest, filter.location?: "",
            filter.isSold, filter.displayOnlyPhotos
        ).observe(viewLifecycleOwner, Observer {estates ->

            val recyclerView = activity?.findViewById<RecyclerView>(R.id.recyclerview)
            val adapter = MainRecyclerViewAdapter(activity?.applicationContext!!)
            recyclerView?.adapter = adapter
            recyclerView?.layoutManager = LinearLayoutManager(activity?.applicationContext!!)

            estates.let {
                adapter.setEstate(it)
            }
        })
    }
}
