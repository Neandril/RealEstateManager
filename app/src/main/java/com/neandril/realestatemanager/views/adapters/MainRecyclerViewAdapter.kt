package com.neandril.realestatemanager.views.adapters

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Log
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
import java.io.Serializable

class MainRecyclerViewAdapter internal constructor(context: Context) : RecyclerView.Adapter<MainRecyclerViewAdapter.EstateViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var estates = emptyList<Estate>()

    inner class EstateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dropdownArrow: ImageButton = itemView.findViewById(R.id.imageView_drop_down_arrow)
        private val thumbnail: ImageView = itemView.findViewById(R.id.main_recyclerview_thumbnail)
        private val typeItemView: TextView = itemView.findViewById(R.id.main_recyclerview_type_textview)
        private val priceItemView: TextView = itemView.findViewById(R.id.main_recyclerview_price_textview)
        private val subItem: ConstraintLayout = itemView.findViewById(R.id.subitem)
        private val surfaceItemView: TextView = itemView.findViewById(R.id.main_recyclerview_surface_textview)
        private val nbBathRoomsItemView: TextView = itemView.findViewById(R.id.main_recyclerview_nbBathRooms_textview)
        private val nbBedRoomsItemView: TextView = itemView.findViewById(R.id.main_recyclerview_nbBedRooms_textview)
        private val nbOtherRoomsItemView: TextView = itemView.findViewById(R.id.main_recyclerview_nbOtherRooms_textview)
        private val agentNameItemView: TextView = itemView.findViewById(R.id.main_recyclerview_agent_textview)
        private val isSoldItemView: TextView = itemView.findViewById(R.id.main_recyclerview_isSold_textview)

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

            val price = estate.price.toThousand()
            val surface = estate.surface.toSquare()

            // Fill the fields
            typeItemView.text = estate.type
            priceItemView.text = price
            surfaceItemView.text = surface
            nbBathRoomsItemView.text = estate.nbBathrooms
            nbBedRoomsItemView.text = estate.nbBedrooms
            nbOtherRoomsItemView.text = estate.nbOtherRooms
            agentNameItemView.text = estate.agentName
            // agentNameItemView.text = estate.addressLatLng

            if (estate.sold) {
                isSoldItemView.text = itemView.context.getString(R.string.has_been_sold)
                isSoldItemView.setTextColor(Color.RED)
            } else {
                isSoldItemView.text = itemView.context.getString(R.string.still_available)
                isSoldItemView.setTextColor(Color.GREEN)
            }

            if (estate.estatePhotos?.get(0)?.image?.toUri() != null) {
                thumbnail.setImageURI(estate.estatePhotos[0].image.toUri())
            } else {
                thumbnail.setImageResource(R.drawable.add_image)
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
            Log.d("onClick", "item clicked: " + current.id)
            val context = holder.itemView.context
            val intent = Intent(context, EstateDetailsActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.putExtra("id_estate", current.id)
            context.startActivity(intent)
        }

        // Handle click on RecyclerView Arrow Button
        holder.dropdownArrow.setOnClickListener {
            Log.d("onClick", "Arrow clicked: $position")
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