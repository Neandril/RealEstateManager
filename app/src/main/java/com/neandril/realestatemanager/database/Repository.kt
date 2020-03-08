package com.neandril.realestatemanager.database

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.neandril.realestatemanager.models.Estate
import kotlin.math.max

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
                    nbRooms: Int, nbPhotos: Int, type: String, points_of_interest: String, location: String,
                    isSold: Boolean)
            : LiveData<List<Estate>> {

        val builder = StringBuilder("SELECT * FROM real_estate_table WHERE ")

        // Price, surface, and number or rooms
        builder.append("(price BETWEEN $minPrice AND $maxPrice)")
        builder.append(" AND (surface BETWEEN $minSurface AND $maxSurface)")
        builder.append(" AND (nbTotalRooms > $nbRooms)")

        // Estate Type
        if (type.isNotEmpty()) {
            when {
                type.split(",").size > 1 -> {
                    // If type contains more than one item
                    builder.append(" AND (type LIKE ")
                    for (i in type.split(",").indices) {
                        builder.append("\"${type.split(",")[i]}\"")
                        if (i < type.split(",").size -1) {
                            builder.append(" OR type LIKE ")
                        }
                    }
                }
                type.split(",").size == 1 -> builder.append(" AND (type LIKE \"$type\"" ) // List contains exactly one item
            }
            builder.append(")")
        }

        // Points of interest
        if (points_of_interest.isNotEmpty()) {
            when {
                points_of_interest.split(",").size > 1 -> {
                    // If points of interest contain more than one item
                    builder.append(" AND (points_of_interest LIKE ")
                    for (i in points_of_interest.split(",").indices) {
                        builder.append("\"%${points_of_interest.split(",")[i]}%\"")
                        if (i < points_of_interest.split(",").size -1) {
                            builder.append(" AND points_of_interest LIKE ")
                        }
                    }
                }
                points_of_interest.split(",").size  == 1 -> builder.append(" AND (points_of_interest LIKE \"%$points_of_interest\"%") // List contains one item
            }
            builder.append(")")
        }

        // Location
        if (location.isNotEmpty()) {
            builder.append(" AND (address LIKE \"$location\")")
        }

        // Is Sold
        if (isSold) {
            builder.append(" AND (sold LIKE 1)")
        }


        builder.toString()

        Log.d("Builder", "Str: $builder")

        return Transformations.map(estateDao.getFiltered(minPrice, maxPrice, minSurface, maxSurface, nbRooms, nbPhotos, type, points_of_interest, location, isSold)) { livedata ->
            livedata
        }
    }

    // suspend fun getMaxPrice(): Int = estateDao.getMaxPrice()
}