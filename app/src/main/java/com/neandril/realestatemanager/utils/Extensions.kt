package com.neandril.realestatemanager.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

fun ViewGroup.inflate(layoutId: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutId, this, attachToRoot)
}

fun Int.paddingZero(): String {
    return String.format("%02d", this)
}

// Returns a string with the "m²" suffix
fun String.toSquare() : String {
    return String.format("$this m²", this)
}

// Returns a string with separated thousands
fun String.toThousand() : String {
    val value = this.toInt()
    return String.format("%,d".format(value) + " $", this)
}