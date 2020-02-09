package com.neandril.realestatemanager.views.fragments


import android.widget.TextView
import com.neandril.realestatemanager.R
import com.neandril.realestatemanager.views.base.BaseFragment

class FragmentEstateDetails : BaseFragment() {

    override fun getFragmentLayout(): Int {
        return R.layout.fragment_estate_details
    }

    override fun configureFragment() {
        this.configureView()
    }

    fun configureView() {
        val blank = activity?.findViewById<TextView>(R.id.blank)
        blank?.text = "ok"
    }

}
