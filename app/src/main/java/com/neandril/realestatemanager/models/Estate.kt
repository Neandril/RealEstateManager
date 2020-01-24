package com.neandril.realestatemanager.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "real_estate_table")
data class Estate (
    @PrimaryKey @ColumnInfo(name = "address") val address: String,
    @ColumnInfo(name = "type") val type: String,
    @ColumnInfo(name = "price") val price: String,
    @ColumnInfo(name = "surface") val surface: String,
    @ColumnInfo(name = "nb_bathrooms") val nbBathrooms: String,
    @ColumnInfo(name = "nb_bedrooms") val nbBedrooms: String,
    @ColumnInfo(name = "nb_other_rooms") val nbOtherRooms: String
) {
    var isExpanded = false
}
