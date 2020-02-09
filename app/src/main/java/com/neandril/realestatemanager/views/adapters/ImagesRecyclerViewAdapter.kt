package com.neandril.realestatemanager.views.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.neandril.realestatemanager.R
import com.neandril.realestatemanager.models.Thumbnail

// Example : Estate("Hello", "Apartment", "1.200.00", "150 m²", "3", "2", "5")

class ImagesRecyclerViewAdapter internal constructor(context: Context) : RecyclerView.Adapter<ImagesRecyclerViewAdapter.ImagesViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    // private var thumbnail = emptyList<Thumbnail>()

    private var thumbnail: MutableList<Thumbnail> = mutableListOf()

    inner class ImagesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val btnDeleteImage: ImageButton = itemView.findViewById(R.id.btn_delete_image)
        private val photoThumb: ImageView = itemView.findViewById(R.id.image_thumbnail)
        private val imageDescription: TextView = itemView.findViewById(R.id.image_description)

        fun bind(thumbnail: Thumbnail) {

            photoThumb.setImageURI(thumbnail.image.toUri())
            imageDescription.text = thumbnail.description

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImagesViewHolder {
        val itemView = inflater.inflate(R.layout.recyclerview_images_item, parent, false)
        return ImagesViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ImagesViewHolder, position: Int) {
        val current = thumbnail[position]

        holder.bind(current)
        // Handle click on RecyclerView Arrow Button
        holder.btnDeleteImage.setOnClickListener {
            Log.d("onClick", "Delete image clicked")
            removeThumbnail(position)
        }
    }

    internal fun setThumbnail(thumbs: MutableList<Thumbnail>) {
        this.thumbnail = thumbs
        notifyDataSetChanged()
    }

    private fun removeThumbnail(position: Int) {
        thumbnail.removeAt(position)
        notifyDataSetChanged()
    }

    override fun getItemCount() = thumbnail.size

}