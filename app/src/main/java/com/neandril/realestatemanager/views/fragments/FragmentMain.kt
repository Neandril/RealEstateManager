package com.neandril.realestatemanager.views.fragments


import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.neandril.realestatemanager.R
import com.neandril.realestatemanager.models.Estate
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

    override fun onResume() {
        super.onResume()

        estateViewModel = ViewModelProvider(this).get(EstateViewModel::class.java)
        if (estateViewModel.allEstates.hasObservers()) {
            Log.d("onResume", "allEstate: true")
        } else {
            Log.d("onResume", "allEstate: false")
        }
        configureRecyclerView()
    }
}
