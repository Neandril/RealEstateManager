package com.neandril.realestatemanager.utils

import androidx.room.TypeConverter
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.neandril.realestatemanager.models.Thumbnail

class Converters {

    private val gson = Gson()
    
    @TypeConverter
    fun fromThumbnailsList(thumbnailsList: List<Thumbnail>?): String? {
        if (thumbnailsList == null) {
            return null
        }
        val type = object : TypeToken<List<Thumbnail>>() {

        }.type
        return gson.toJson(thumbnailsList, type)
    }

    @TypeConverter
    fun toThumbnailsList(thumbnails: String?): List<Thumbnail>? {
        if (thumbnails == null) {
            return null
        }
        val type = object : TypeToken<List<Thumbnail>>() {

        }.type
        return gson.fromJson(thumbnails, type)
    }

}