package com.neandril.realestatemanager.models

import java.io.Serializable

data class FilterModel (
    var minPrice: Int,
    var maxPrice: Int,
    var minSurface: Int,
    var maxSurface: Int,
    var nbRooms: Int?,
    // var nbPhotos: Int?,
    var estateType: MutableList<String>?,
    var estatePois: MutableList<String>?
    // var location: String?,
    // var isSold: Boolean,
    // var soldDate: String?
) : Serializable