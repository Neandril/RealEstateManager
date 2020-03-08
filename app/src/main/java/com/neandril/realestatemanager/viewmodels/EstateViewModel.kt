package com.neandril.realestatemanager.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.neandril.realestatemanager.database.RealEstateRoomDatabase
import com.neandril.realestatemanager.database.Repository
import com.neandril.realestatemanager.models.Estate
import kotlinx.coroutines.launch

class EstateViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: Repository

    val allEstates: LiveData<List<Estate>>

    init {
        val estateDao = RealEstateRoomDatabase.getDatabase(application, viewModelScope).estateDao()
        repository = Repository(estateDao)
        allEstates = repository.allEstates
    }

    fun insert(estate: Estate) = viewModelScope.launch {
        repository.insert(estate)
    }

    fun getSingleEstate(id: Int) = repository.getSingleEstate(id)

    fun updateEstate(estate: Estate) = viewModelScope.launch {
        repository.updateEstate(estate)
    }


    fun getFiltered(minPrice: Int, maxPrice: Int,
                    minSurface: Int, maxSurface: Int,
                    nbRooms: Int, nbPhotos: Int, type: String, points_of_interest: String, location: String,
                    isSold: Boolean)
            = repository.getFiltered(minPrice, maxPrice, minSurface, maxSurface, nbRooms, nbPhotos, type, points_of_interest, location, isSold)

/*    fun getMaxPrice() = viewModelScope.launch {
        repository.getMaxPrice()
    }*/
}