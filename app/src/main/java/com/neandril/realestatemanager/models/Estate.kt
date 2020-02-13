package com.neandril.realestatemanager.models

import android.graphics.Bitmap
import androidx.room.*
import com.google.android.gms.maps.model.LatLng
import java.io.Serializable

@Entity(tableName = "real_estate_table")
data class Estate (
    @PrimaryKey(autoGenerate = true) val id: Int,
    val address: String,
    val addressLatLng: String,
    val type: String,
    val price: String,
    val surface: String,
    val nbBathrooms: String,
    val nbBedrooms: String,
    val nbOtherRooms: String,
    val agentName: String,
    val sold: Boolean,
    val soldDate: String,
    val description: String,
    val estatePhotos: List<Thumbnail>?,
    val addressThumbnail: Bitmap?
) : Serializable {
    var isExpanded = false
}
