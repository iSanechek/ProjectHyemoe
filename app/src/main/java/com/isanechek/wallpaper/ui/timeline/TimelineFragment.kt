package com.isanechek.wallpaper.ui.timeline

import _color
import _layout
import android.os.Bundle
import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DiffUtil
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.isanechek.common.models.Wallpaper
import com.isanechek.extensions.*
import com.isanechek.repository.Status
import com.isanechek.wallpaper.ui.base.BaseFragment
import com.isanechek.wallpaper.ui.details.DetailScreen
import com.isanechek.wallpaper.ui.widgets.navigation.NavigationId
import com.isanechek.wallpaper.utils.network.Connection
import com.list.rados.fastlist.bind
import com.squareup.picasso.Picasso
import com.yandex.mobile.ads.AdRequest
import com.yandex.mobile.ads.AdRequestError
import com.yandex.mobile.ads.nativeads.NativeAdLoader
import com.yandex.mobile.ads.nativeads.NativeAdLoaderConfiguration
import com.yandex.mobile.ads.nativeads.NativeAppInstallAd
import com.yandex.mobile.ads.nativeads.NativeContentAd
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator
import kotlinx.android.synthetic.main.progress_error_layout.*
import kotlinx.android.synthetic.main.timeline_item_layout.view.*
import kotlinx.android.synthetic.main.timeline_screen_layout.*
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * Created by isanechek on 9/26/17.
 */
private const val TAG = "TimelineFragment"

class TimelineFragment : BaseFragment(), SwipeRefreshLayout.OnRefreshListener {

    private val viewModel: TimelineViewModel by viewModel()
    private var initialLoading = false

    private val screenTitle: String
        get() = this extraWithKey "timeline.screen.title"
                ?: emptyString

    private val wallpaperKey: String
        get() = this extraWithKey "timeline.screen.wall.key"
                ?: emptyString

    private val wallpaperPath: String
        get() = this extraWithKey "timeline.screen.wall.path"
                ?: emptyString


    // ads
    private lateinit var nativeAdLoader: NativeAdLoader

    private val diffCallback = object : DiffUtil.ItemCallback<Wallpaper>() {
        override fun areItemsTheSame(oldItem: Wallpaper, newItem: Wallpaper): Boolean =
                oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Wallpaper, newItem: Wallpaper): Boolean =
                oldItem == newItem
    }

    override fun layoutResId(): Int = _layout.timeline_screen_layout

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progress_view_progress.progressColor = _color.my_primary_dark_color
        with(list_rv) {
            activity.setupWaterfall(this)
            setHasFixedSize(true)
            val animator = SlideInUpAnimator(OvershootInterpolator(1f))
            animator.addDuration = 350
            animator.moveDuration = 150
            itemAnimator = animator
        }
        setupSwipeRefresh()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.loadWallpapers2(wallpaperKey, wallpaperPath)
        setupRequestObserver()
        createNativeAdLoader()
    }

    override fun onRefresh() {
        viewModel.refresh()
    }

    override fun getTitle(): String = if (screenTitle == "") NavigationId.TIMELINE.name else screenTitle

    private fun setupRequestObserver() {
        viewModel.networkState.observe(this, Observer { state ->
            state ?: return@Observer

            when(state.status) {
                Status.INITIAL -> {
                    initialLoading = true
                    progress_view_progress.start()
                }
                Status.RUNNING -> {}
                Status.SUCCESS -> {
                    progress_view_progress.stop()
                }
                Status.FAILED -> {}
                Status.BAD_REQUEST -> {}
                Status.NOT_FIND -> {}
            }
        })

        viewModel.data.observe(this, Observer { data ->
            data ?: return@Observer
            list_rv.bind(diffCallback, _layout.timeline_item_layout) { wallpaper: Wallpaper ->
                Picasso.get()
                        .load(wallpaper.previewUrl)
                        .into(list_item_cover)

                list_item_cover.onClick {
                    DetailScreen.create(activity, wallpaper)
                }
            }.submitList(data)
        })



        connection.observe(this, Observer { conn ->
            conn ?: return@Observer
            when(conn.type) {
                Connection.WIFI -> {
                    refreshDisable(false)
//                    viewModel.load(category, RequestStrategy.DATA_REQUEST)
                }
                Connection.MOBILE -> {
                    refreshDisable(false)
//                    viewModel.load(category, RequestStrategy.DATA_REQUEST)
                }
                Connection.OFFLINE -> {
                    refreshDisable()
                }
            }
        })
    }

    private fun refreshDisable(disable: Boolean = true) {
        list_refresh.isEnabled = !disable
    }

    private fun hideProgress() {
        if (list_refresh.isRefreshing) list_refresh.isRefreshing = false
    }

    private fun setupSwipeRefresh() {
        with(list_refresh) {
            setOnRefreshListener(this@TimelineFragment)
            setColorSchemeColors(ContextCompat.getColor(activity, _color.my_primary_dark_color))
        }
    }


    private fun createNativeAdLoader() {
        val adLoadConfig = NativeAdLoaderConfiguration
            .Builder("R-M-268309-1"                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    , false)
            .setImageSizes(NativeAdLoaderConfiguration.NATIVE_IMAGE_SIZE_LARGE)
            .build()

        nativeAdLoader = NativeAdLoader(activity.applicationContext, adLoadConfig)
        nativeAdLoader.setOnLoadListener(nativeAdLoadListenr)
        nativeAdLoader.loadAd(AdRequest.builder().build())
    }

    private val nativeAdLoadListenr = object: NativeAdLoader.OnLoadListener {
        override fun onContentAdLoaded(p0: NativeContentAd) {
//            _adapter?.setAd(p0)
        }

        override fun onAppInstallAdLoaded(p0: NativeAppInstallAd) {
//            _adapter?.setAd(p0)
        }

        override fun onAdFailedToLoad(p0: AdRequestError) {}
    }
}