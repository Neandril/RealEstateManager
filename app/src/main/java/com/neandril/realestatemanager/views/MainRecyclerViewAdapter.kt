package com.neandril.realestatemanager.views

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.neandril.realestatemanager.R
import com.neandril.realestatemanager.models.Estate

class MainRecyclerViewAdapter internal constructor(context: Context) : RecyclerView.Adapter<MainRecyclerViewAdapter.EstateViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var estates = emptyList<Estate>()

    inner class EstateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dropdownArrow: ImageButton = itemView.findViewById(R.id.imageView_drop_down_arrow)
        private val thumbnail: ImageView = itemView.findViewById(R.id.main_recyclerview_thumbnail)
        private val addressItemView: TextView = itemView.findViewById(R.id.main_recyclerview_address_textview)
        private val priceItemView: TextView = itemView.findViewById(R.id.main_recyclerview_price_textview)
        private val subitem: View = itemView.findViewById(R.id.subitem)

        fun bind(estate: Estate) {
            thumbnail.requestLayout()
            val expanded: Boolean = estate.isExpanded

            if (expanded) {
                // Configure widget when expanded
                subitem.visibility = View.VISIBLE
                dropdownArrow.setImageResource(R.drawable.ic_arrow_drop_up)
                thumbnail.layoutParams.height = 400
                thumbnail.layoutParams.width = 400
            } else {
                // Configure widget when collapsed
                subitem.visibility = View.GONE
                dropdownArrow.setImageResource(R.drawable.ic_arrow_drop_down)
                thumbnail.layoutParams.height = 200
                thumbnail.layoutParams.width = 200
            }

            addressItemView.text = estate.address
            priceItemView.text = estate.price
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EstateViewHolder {
        val itemView = inflater.inflate(R.layout.main_recycler_view_item, parent, false)
        return EstateViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: EstateViewHolder, position: Int) {
        val current = estates[position]

        holder.bind(current)
        // Handle click on RecyclerView Item
        holder.itemView.setOnClickListener {
            Log.d("onClick", "item clicked: $position")

        }

        // Handle click on RecyclerView Arrow Button
        holder.dropdownArrow.setOnClickListener {
            Log.d("onClick", "Arrow clicked: $position")
            val expanded: Boolean = current.isExpanded
            current.isExpanded = !expanded
            notifyItemChanged(position)
        }
    }

    internal fun setEstate(words: List<Estate>) {
        this.estates = words
        notifyDataSetChanged()
    }

    override fun getItemCount() = estates.size

}