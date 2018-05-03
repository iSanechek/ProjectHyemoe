package com.isanechek.wallpaper.view.main.fragments.timeline.ads

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.isanechek.wallpaper.utils._id
import com.isanechek.wallpaper.utils._layout
import com.isanechek.wallpaper.utils.extensions.inflate
import com.yandex.mobile.ads.nativeads.NativeGenericAd
import com.yandex.mobile.ads.nativeads.template.NativeBannerView

/**
 * Created by isanechek on 5/1/18.
 */
class AdsTimelineHolder(parent: ViewGroup?) : RecyclerView.ViewHolder(parent?.inflate(_layout.timeline_ads_item_layout)) {

    val banner: NativeBannerView = itemView.findViewById(_id.timeline_ads_item)

    fun bindTo(nativeAd: NativeGenericAd?) {
        if (nativeAd != null) {
            banner.setAd(nativeAd)
            nativeAd.loadImages()
        }
    }
}