package com.neandril.realestatemanager.models

import android.graphics.Bitmap
import androidx.room.*
import com.google.android.gms.maps.model.LatLng
import java.io.Serializable

@Entity(tableName = "real_estate_table")
data class Estate (
    @PrimaryKey(autoGenerate = true) var id: Int,
    val address: String,
    val addressLatLng: String,
    val type: String,
    @ColumnInfo(name = "price") val price: Int,
    val surface: String,
    val nbBathrooms: String,
    val nbBedrooms: String,
    val nbTotalRooms: String,
    val agentName: String,
    val sold: Boolean,
    val soldDate: String,
    val description: String,
    val estatePhotos: List<Thumbnail>?,
    val addressThumbnail: Bitmap?,
    val points_of_interest: String?
) : Serializable {
    var isExpanded = false // Used to determine whether the view is expanded
}
