package com.neandril.realestatemanager.views.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.neandril.realestatemanager.R

class MyListAdapter (private val context: Activity, private val title: Array<String>, private val imgid: Array<Int>)
    : ArrayAdapter<String>(context, R.layout.custom_list, title)  {

    @SuppressLint("ViewHolder", "InflateParams")
    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        val inflater = context.layoutInflater
        val rowView = inflater.inflate(R.layout.custom_list, null, true)

        val titleText = rowView.findViewById(R.id.list_item_title) as TextView
        val imageView = rowView.findViewById(R.id.list_item_img) as ImageView

        titleText.text = title[position]
        imageView.setImageResource(imgid[position])

        return rowView
    }

}