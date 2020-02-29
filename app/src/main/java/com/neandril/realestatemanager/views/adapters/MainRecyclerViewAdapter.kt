package com.neandril.realestatemanager.views.adapters

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.neandril.realestatemanager.R
import com.neandril.realestatemanager.models.Estate
import com.neandril.realestatemanager.utils.toSquare
import com.neandril.realestatemanager.utils.toThousand
import com.neandril.realestatemanager.views.activities.EstateDetailsActivity

class MainRecyclerViewAdapter internal constructor(context: Context) : RecyclerView.Adapter<MainRecyclerViewAdapter.EstateViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var estates = emptyList<Estate>()

    companion object {
        const val ID_ESTATE = "id_estate"
        const val ESTATE_PRICE = "estate_price"
    }

    inner class EstateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dropdownArrow: ImageButton = itemView.findViewById(R.id.imageView_drop_down_arrow)
        private val thumbnail: ImageView = itemView.findViewById(R.id.main_recyclerview_thumbnail)
        private val typeItemView: TextView = itemView.findViewById(R.id.main_recyclerview_type_textview)
        private val priceItemView: TextView = itemView.findViewById(R.id.main_recyclerview_price_textview)
        private val subItem: ConstraintLayout = itemView.findViewById(R.id.subitem)
        private val surfaceItemView: TextView = itemView.findViewById(R.id.main_recyclerview_surface_textview)
        private val nbBathRoomsItemView: TextView = itemView.findViewById(R.id.main_recyclerview_nbBathRooms_textview)
        private val nbBedRoomsItemView: TextView = itemView.findViewById(R.id.main_recyclerview_nbBedRooms_textview)
        private val nbTotalRoomsItemView: TextView = itemView.findViewById(R.id.main_recyclerview_nbRooms_textview)
        private val agentNameItemView: TextView = itemView.findViewById(R.id.main_recyclerview_agent_textview)
        private val isSoldItemView: TextView = itemView.findViewById(R.id.main_recyclerview_isSold_textview)
        private val soldDateTextView: TextView = itemView.findViewById(R.id.main_recyclerview_soldDate)

        fun bind(estate: Estate) {
            thumbnail.requestLayout()
            val expanded: Boolean = estate.isExpanded

            if (expanded) {
                // Configure widgets when expanded
                subItem.visibility = View.VISIBLE
                dropdownArrow.setImageResource(R.drawable.ic_arrow_drop_up)
                thumbnail.layoutParams.height = 400
                thumbnail.layoutParams.width = 400
            } else {
                // Configure widgets when collapsed
                subItem.visibility = View.GONE
                dropdownArrow.setImageResource(R.drawable.ic_arrow_drop_down)
                thumbnail.layoutParams.height = 200
                thumbnail.layoutParams.width = 200
            }

            val price = estate.price.toString().toThousand()
            val surface = estate.surface.toSquare()

            // Fill the fields
            typeItemView.text = estate.type
            priceItemView.text = price
            surfaceItemView.text = surface
            nbBathRoomsItemView.text = estate.nbBathrooms
            nbBedRoomsItemView.text = estate.nbBedrooms
            nbTotalRoomsItemView.text = estate.nbTotalRooms
            agentNameItemView.text = estate.agentName

            if (estate.sold) {
                isSoldItemView.text = itemView.context.getString(R.string.has_been_sold)
                isSoldItemView.setTextColor(Color.RED)
                soldDateTextView.visibility = View.VISIBLE
                soldDateTextView.text = estate.soldDate
            } else {
                isSoldItemView.text = itemView.context.getString(R.string.still_available)
                isSoldItemView.setTextColor(Color.GREEN)
                soldDateTextView.visibility = View.GONE
            }

            if (estate.estatePhotos != null) {
                thumbnail.setImageURI(estate.estatePhotos[0].image.toUri())
            } else {
                thumbnail.setImageResource(R.drawable.no_preview_available)
            }
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
            current.isExpanded = false // Reset state of expanded view
            val context = holder.itemView.context
            val intent = Intent(context, EstateDetailsActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

            intent.putExtra(ID_ESTATE, current.id)
            intent.putExtra(ESTATE_PRICE, current.price)
            context.startActivity(intent)
        }

        // Handle click on RecyclerView Arrow Button
        holder.dropdownArrow.setOnClickListener {
            val expanded: Boolean = current.isExpanded
            current.isExpanded = !expanded
            notifyItemChanged(position)
        }
    }

    internal fun setEstate(estate: List<Estate>) {
        this.estates = estate
        notifyDataSetChanged()
    }

    override fun getItemCount() = estates.size

}