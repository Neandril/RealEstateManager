package com.neandril.realestatemanager.utils

import android.graphics.Bitmap
import android.text.TextUtils
import android.util.Base64
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.neandril.realestatemanager.models.Thumbnail
import java.io.ByteArrayOutputStream
import android.graphics.BitmapFactory

class Converters {

    private val gson = Gson()

    /**
     * Convert the thumbnails list to string value
     */
    @TypeConverter
    fun fromThumbnailsList(thumbnailsList: List<Thumbnail>?): String? {
        if (thumbnailsList == null) {
            return null
        }
        val type = object : TypeToken<List<Thumbnail>>() {

        }.type
        return gson.toJson(thumbnailsList, type)
    }

    /**
     * Convert string to list of thumbnails
     */
    @TypeConverter
    fun toThumbnailsList(thumbnails: String?): List<Thumbnail>? {
        if (thumbnails == null) {
            return null
        }
        val type = object : TypeToken<List<Thumbnail>>() {

        }.type
        return gson.fromJson(thumbnails, type)
    }

    /**
     * Convert base64 encoded string to a bitmap
     */
    @TypeConverter
    fun fromBase64ToBitmap(base64Value: String?): Bitmap? {
        return if (!TextUtils.isEmpty(base64Value)) {
            val decodedBytes = Base64.decode(base64Value, 0)
            BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        } else {
            null
        }
    }

    /**
     * Convert a bitmap to an encoded base64 string
     */
    @TypeConverter
    fun fromBitmapToBase64(bitmap: Bitmap?) : String? {
        return if (bitmap != null) {
            val byteArrayOS = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 25, byteArrayOS)
            Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT)
        } else {
            null
        }
    }
}