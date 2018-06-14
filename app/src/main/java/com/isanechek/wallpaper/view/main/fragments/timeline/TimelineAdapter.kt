package com.isanechek.wallpaper.view.main.fragments.timeline

import android.arch.paging.PagedListAdapter
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.isanechek.wallpaper.data.database.Wallpaper
import com.isanechek.wallpaper.view.main.fragments.timeline.ads.AdsTimelineHolder
import com.yandex.mobile.ads.nativeads.NativeGenericAd

/**
 * Created by isanechek on 5/1/18.
 */

const val ITEM_VIEW_HOLDER = 0
const val ADS_VIEW_HOLDER = 1

class TestAdapter : PagedListAdapter<Wallpaper, RecyclerView.ViewHolder>(diffCallback) {

    private var listener: ItemClickListener? = null
    private var ads: NativeGenericAd? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when {
            viewType == ITEM_VIEW_HOLDER -> return TimelineHolder(parent)
            viewType == ADS_VIEW_HOLDER -> return AdsTimelineHolder(parent)
            else -> throw IllegalArgumentException("Hz timeline item type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is TimelineHolder -> holder.bindTo(getItem(position), listener, position)
            is AdsTimelineHolder -> holder.bindTo(ads)
        }
    }

    override fun getItemViewType(position: Int): Int {
        val adsPos = arrayOf(10, 20, 30, 40, 50)
        return if (adsPos.contains(position) && ads != null) {
            ADS_VIEW_HOLDER
        } else ITEM_VIEW_HOLDER
    }

    fun setAd(ads: NativeGenericAd) {
        this.ads = ads
    }

    fun setOnClickItemListener(listener: ItemClickListener) {
        this.listener = listener
    }

    interface ItemClickListener {
        fun onItemClick(view: View, position: Int, id: String, wallpaper: Wallpaper)
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<Wallpaper>() {
            override fun areItemsTheSame(oldItem: Wallpaper, newItem: Wallpaper): Boolean =
                oldItem.title == newItem.title

            override fun areContentsTheSame(oldItem: Wallpaper, newItem: Wallpaper): Boolean =
                oldItem == newItem
        }
    }
}