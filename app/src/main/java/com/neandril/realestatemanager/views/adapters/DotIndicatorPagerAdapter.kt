package com.neandril.realestatemanager.views.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
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
        val imageView = item.findViewById(R.id.item_image) as ImageView

        imageView.setImageURI(imgList?.get(position)?.image?.toUri())

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