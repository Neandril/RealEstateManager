package com.neandril.realestatemanager.views.base

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.neandril.realestatemanager.models.Estate
import com.neandril.realestatemanager.models.FilterModel
import com.neandril.realestatemanager.viewmodels.EstateViewModel


abstract class BaseFragment: Fragment() {

    companion object {
        private val TAG = BaseFragment::class.java.simpleName
    }

    protected abstract fun getFragmentLayout(): Int
    protected abstract fun configureFragment()

    fun BaseFragment() { // Required empty constructor
    }

    @Nullable
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(getFragmentLayout(), container, false)
        Log.d(TAG, "onCreateView: Fragment created")
        configureFragment()
        return view
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy: Fragment destroyed")
        super.onDestroy()
    }

}