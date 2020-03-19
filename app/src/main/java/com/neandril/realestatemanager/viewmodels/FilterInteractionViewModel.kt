package com.neandril.realestatemanager.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.neandril.realestatemanager.models.FilterModel

class FilterInteractionViewModel : ViewModel() {

    private val filterModel: MutableLiveData<FilterModel> = MutableLiveData()

    fun observeFilter(): LiveData<FilterModel> = filterModel

    fun setFilter(filterModel: FilterModel) {
        this.filterModel.postValue(filterModel)
    }

    fun getFilter() : LiveData<FilterModel> = filterModel
}