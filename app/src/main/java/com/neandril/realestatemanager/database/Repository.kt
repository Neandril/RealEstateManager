package com.neandril.realestatemanager.database

import androidx.lifecycle.LiveData
import com.neandril.realestatemanager.models.Estate

class Repository(private val estateDao: EstateDao) {

    val allEstates: LiveData<List<Estate>> = estateDao.getAllEstates()

    fun getSingleEstate(id: Int): LiveData<Estate> = estateDao.getSingleEstate(id)

    suspend fun insert(estate: Estate) {
        estateDao.insert(estate)
    }
}