package com.neandril.realestatemanager.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.neandril.realestatemanager.database.DbFactory
import com.neandril.realestatemanager.database.RealEstateRoomDatabase
import com.neandril.realestatemanager.database.Repository
import com.neandril.realestatemanager.models.Estate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EstateViewModel(val context: Application) : AndroidViewModel(context) {

    private val repository: Repository by lazy { Repository(estateDao) }
    private val estateDao by lazy { DbFactory.getDatabase(context, viewModelScope).estateDao() }
    private val allEstates: MutableLiveData<List<Estate>> = MutableLiveData()

    // Init the viewmodel
    fun init() {
        // Get all estates using coroutine
        viewModelScope.launch(Dispatchers.Main) {
            // withContext : change thread
            allEstates.postValue(withContext(Dispatchers.IO) { repository.getAllEstate() } )
        }
    }

    fun getEstates() : LiveData<List<Estate>> = allEstates

    fun estateByPrice(): LiveData<List<Estate>> = repository.getEstateByPrice

    fun estateBySurface(): LiveData<List<Estate>> = repository.getEstateBySurface

    fun insert(estate: Estate) = viewModelScope.launch {
        repository.insert(estate)
    }

    fun getSingleEstate(id: Int) = repository.getSingleEstate(id)

    fun updateEstate(estate: Estate) = viewModelScope.launch {
        repository.updateEstate(estate)
    }

    fun getFiltered(minPrice: Int, maxPrice: Int,
                    minSurface: Int, maxSurface: Int,
                    nbRooms: Int, type: String, points_of_interest: String, location: String,
                    isSold: Boolean, displayOnlyPhotos: Boolean) {

        viewModelScope.launch(Dispatchers.IO) { allEstates.postValue(repository.getFiltered(minPrice, maxPrice, minSurface, maxSurface, nbRooms, type, points_of_interest, location, isSold, displayOnlyPhotos)) }
    }

}