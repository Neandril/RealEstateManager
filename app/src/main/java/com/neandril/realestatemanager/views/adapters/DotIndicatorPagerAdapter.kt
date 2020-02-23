package com.neandril.realestatemanager.views.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.net.toUri
import androidx.viewpager.widget.PagerAdapter
import com.neandril.realestatemanager.R
import com.neandril.realestatemanager.models.Thumbnail

class DotIndicatorPagerAdapter(thumbnail: List<Thumbnail>?) : PagerAdapter() {

    private var imgList: List<Thumbnail>? = thumbnail

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val item = LayoutInflater.from(container.context).inflate(
            R.layout.viewpager_item, container,
            false)
        val imageView: ImageView = item.findViewById(R.id.item_image)
        val imageOverlay: TextView = item.findViewById(R.id.item_image_overlay)

        imageView.setImageURI(imgList?.get(position)?.image?.toUri())
        imageOverlay.text = imgList?.get(position)?.description

        item.setOnClickListener {
            Log.d("ViewPager", "clicked: " + imgList?.get(position)?.description)
            val viewGroup = item.findViewById<ViewGroup>(android.R.id.content)

            val dialogView = LayoutInflater.from(item.context).inflate(R.layout.custom_dialog_pic_viewer, viewGroup, false)
            val builder = AlertDialog.Builder(item.context)

            //setting the view of the builder to our custom view that we already inflated
            builder.setView(dialogView)

            //finally creating the alert dialog and displaying it
            val alertDialog = builder.create()
            alertDialog.show()

            val picViewer = dialogView.findViewById<ImageView>(R.id.pic_viewer)
            picViewer.setImageURI(imgList?.get(position)?.image?.toUri())
        }

        container.addView(item)

        Log.d("dotIndicator", "Nb: " + imgList?.size)
        return item
    }

    override fun getCount(): Int {
        return if (imgList?.isNotEmpty()!!) {
            imgList!!.size
        } else {
            1
        }
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

}