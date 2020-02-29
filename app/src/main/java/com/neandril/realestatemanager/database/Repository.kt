package com.neandril.realestatemanager.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.neandril.realestatemanager.models.Estate

class Repository(private val estateDao: EstateDao) {

    val allEstates: LiveData<List<Estate>> = estateDao.getAllEstates()

    fun getSingleEstate(id: Int): LiveData<Estate> = estateDao.getSingleEstate(id)

    suspend fun insert(estate: Estate) {
        estateDao.insert(estate)
    }

    suspend fun updateEstate(estate: Estate) {
        estateDao.updateEstate(estate)
    }

    fun getFiltered(minPrice: Int, maxPrice: Int,
                    minSurface: Int, maxSurface: Int,
                    nbRooms: Int, type: String, points_of_interest: String)
            : LiveData<List<Estate>> {
        return Transformations.map(estateDao.getFiltered(minPrice, maxPrice, minSurface, maxSurface, nbRooms, type, points_of_interest)) { livedata ->
            livedata
        }
    }

    // suspend fun getMaxPrice(): Int = estateDao.getMaxPrice()
}