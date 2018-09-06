package com.isanechek.wallpaper.ui.timeline.ads

import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import com.isanechek.extensions.inflate
import com.yandex.mobile.ads.nativeads.NativeGenericAd
import com.yandex.mobile.ads.nativeads.template.NativeBannerView

/**
 * Created by isanechek on 5/1/18.
 */
class AdsTimelineHolder(parent: ViewGroup?) : RecyclerView.ViewHolder(parent?.inflate(_layout.timeline_ads_item_layout)!!) {

    private val banner: NativeBannerView = itemView.findViewById(_id.timeline_ads_item)

    fun bindTo(nativeAd: NativeGenericAd?) {
        if (nativeAd != null) {
            banner.setAd(nativeAd)
            nativeAd.loadImages()
        }
    }
}