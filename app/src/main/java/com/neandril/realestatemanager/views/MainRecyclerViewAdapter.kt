package com.neandril.realestatemanager.views

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.neandril.realestatemanager.R
import com.neandril.realestatemanager.models.Estate

class MainRecyclerViewAdapter internal constructor(context: Context) : RecyclerView.Adapter<MainRecyclerViewAdapter.EstateViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var estates = emptyList<Estate>()

    inner class EstateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val addressItemView: TextView = itemView.findViewById(R.id.main_recyclerview_address_textview)
        val priceItemView: TextView = itemView.findViewById(R.id.main_recyclerview_price_textview)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EstateViewHolder {
        val itemView = inflater.inflate(R.layout.main_recycler_view_item, parent, false)
        return EstateViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: EstateViewHolder, position: Int) {
        val current = estates[position]
        holder.addressItemView.text = current.address
        holder.priceItemView.text = current.price
    }

    internal fun setEstate(words: List<Estate>) {
        this.estates = words
        notifyDataSetChanged()
    }

    override fun getItemCount() = estates.size

}