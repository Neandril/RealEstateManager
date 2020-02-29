package com.neandril.realestatemanager.views.fragments

import android.content.Context
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.neandril.realestatemanager.R
import com.neandril.realestatemanager.models.FilterModel
import com.neandril.realestatemanager.viewmodels.EstateViewModel
import com.neandril.realestatemanager.views.adapters.MainRecyclerViewAdapter
import com.neandril.realestatemanager.views.base.BaseFragment

class FragmentMain : BaseFragment() {

    private lateinit var estateViewModel: EstateViewModel

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

        estateViewModel = ViewModelProvider(this).get(EstateViewModel::class.java)
        estateViewModel.allEstates.observe(this, Observer { estates ->
            estates.let { adapter.setEstate(it) }
        })
    }

    fun updateRecyclerView(filters: FilterModel) {

        Log.d("FromFragment", "Filter: price range: " + filters.minPrice + " - " + filters.maxPrice + "\n" +
                "surface range: " + filters.minSurface + " - " + filters.maxSurface + "\n" +
                "rooms: " + filters.nbRooms +
                "type: " + filters.estateType + "\n" +
                "pois: " + filters.estatePois + "\n")

        /*estateViewModel = ViewModelProvider(this).get(EstateViewModel::class.java)
        estateViewModel.allEstates.removeObservers(this)*/

/*        estateViewModel = ViewModelProvider(this).get(EstateViewModel::class.java)
        estateViewModel.getFiltered(filters.minPrice, filters.maxPrice,
            filters.minSurface, filters.maxSurface, filters.nbRooms!!,
            filters.estateType?.get(0).toString(), filters.estatePois?.get(0).toString())*/
    }

    override fun onResume() {
        super.onResume()

        if (this.arguments?.getSerializable("filters") != null) {

            Log.d("onResume", "non null")

        } else {
            Log.d("onResume", "null")
        }
/*        Log.d("FromFragment", "Filter: price range: " + filters.minPrice + " - " + filters.maxPrice + "\n" +
                "surface range: " + filters.minSurface + " - " + filters.maxSurface + "\n" +
                "rooms: " + filters.nbRooms +
                "type: " + filters.estateType + "\n" +
                "pois: " + filters.estatePois + "\n")*/


        configureRecyclerView()
    }
}
