package com.isanechek.wallpaper.view.main.fragments.timeline

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.isanechek.wallpaper.R
import com.isanechek.wallpaper.data.database.Wallpaper
import com.isanechek.wallpaper.view.widgets.ParallaxImageView

/**
 * Created by isanechek on 9/25/17.
 */
class TimelineHolder(parent: ViewGroup?) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent?.context).inflate(R.layout.timeline_item_layout, parent, false)) {
    private val image = itemView.findViewById<ParallaxImageView>(R.id.list_item_img)
    private var model: Wallpaper? = null

    fun bindTo(model: Wallpaper?, listener: TimelineAdapter.ItemClickListener?, position: Int) {
        this.model = model
        model?.preview?.let { Glide.with(image.context).asBitmap().load(it).into(image) }
        val id = model?.title?:""
        image.setOnClickListener { listener?.onItemClick(it, position, id, model!!) }
    }
}