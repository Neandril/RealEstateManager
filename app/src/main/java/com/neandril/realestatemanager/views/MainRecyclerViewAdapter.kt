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
// Example : Estate("Hello", "Apartment", "1.200.00", "150 m²", "3", "2", "5")

class MainRecyclerViewAdapter internal constructor(context: Context) : RecyclerView.Adapter<MainRecyclerViewAdapter.EstateViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var estates = emptyList<Estate>()

    inner class EstateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dropdownArrow: ImageButton = itemView.findViewById(R.id.imageView_drop_down_arrow)
        private val thumbnail: ImageView = itemView.findViewById(R.id.main_recyclerview_thumbnail)
        private val addressItemView: TextView = itemView.findViewById(R.id.main_recyclerview_address_textview)
        private val typeItemView: TextView = itemView.findViewById(R.id.main_recyclerview_type_textview)
        private val priceItemView: TextView = itemView.findViewById(R.id.main_recyclerview_price_textview)
        private val subItem: View = itemView.findViewById(R.id.subitem)
        private val surfaceItemView: TextView = itemView.findViewById(R.id.main_recyclerview_surface_textview)
        private val nbBathRoomsItemView: TextView = itemView.findViewById(R.id.main_recyclerview_nbBathRooms_textview)
        private val nbBedRoomsItemView: TextView = itemView.findViewById(R.id.main_recyclerview_nbBedRooms_textview)
        private val nbOtherRoomsItemView: TextView = itemView.findViewById(R.id.main_recyclerview_nbOtherRooms_textview)

        fun bind(estate: Estate) {
            thumbnail.requestLayout()
            val expanded: Boolean = estate.isExpanded

            if (expanded) {
                // Configure widget when expanded
                subItem.visibility = View.VISIBLE
                dropdownArrow.setImageResource(R.drawable.ic_arrow_drop_up)
                thumbnail.layoutParams.height = 400
                thumbnail.layoutParams.width = 400
            } else {
                // Configure widget when collapsed
                subItem.visibility = View.GONE
                dropdownArrow.setImageResource(R.drawable.ic_arrow_drop_down)
                thumbnail.layoutParams.height = 200
                thumbnail.layoutParams.width = 200
            }

            typeItemView.text = estate.type
            addressItemView.text = estate.address
            priceItemView.text = estate.price
            surfaceItemView.text = estate.surface
            nbBathRoomsItemView.text = estate.nbBathrooms
            nbBedRoomsItemView.text = estate.nbBedrooms
            nbOtherRoomsItemView.text = estate.nbOtherRooms
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