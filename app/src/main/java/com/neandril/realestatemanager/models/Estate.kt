package com.neandril.realestatemanager.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "real_estate_table")
data class Estate (
    @PrimaryKey @ColumnInfo(name = "address") val address: String,
    @ColumnInfo(name = "price") val price: String
)
