package com.isanechek.wallpaper.view.main.fragments.timeline

import android.arch.paging.PagedListAdapter
import android.support.v7.util.DiffUtil
import android.view.View
import android.view.ViewGroup
import com.isanechek.wallpaper.data.database.Wallpaper

/**
 * Created by isanechek on 9/25/17.
 */
class TimelineAdapter : PagedListAdapter<Wallpaper, TimelineHolder>(diffCallback) {

    private var listener: ItemClickListener? = null

    override fun onBindViewHolder(holder: TimelineHolder, position: Int) {
//        holder.bindTo(getItem(position), listener, position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimelineHolder =
        TimelineHolder(parent)

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<Wallpaper>() {
            override fun areItemsTheSame(oldItem: Wallpaper, newItem: Wallpaper): Boolean =
                oldItem.title == newItem.title

            override fun areContentsTheSame(oldItem: Wallpaper, newItem: Wallpaper): Boolean =
                oldItem == newItem
        }
    }

    fun setOnClickItemListener(listener: ItemClickListener) {
        this.listener = listener
    }

    interface ItemClickListener {
        fun onItemClick(view: View, position: Int, id: String, wallpaper: Wallpaper)
    }
}