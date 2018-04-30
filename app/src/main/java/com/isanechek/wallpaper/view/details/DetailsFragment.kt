package com.isanechek.wallpaper.view.details

import android.Manifest
import android.app.WallpaperManager
import android.arch.lifecycle.Observer
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import com.github.chrisbanes.photoview.PhotoView
import com.github.florent37.kotlin.pleaseanimate.please
import com.isanechek.wallpaper.BuildConfig
import com.isanechek.wallpaper.data.DownloadService
import com.isanechek.wallpaper.data.database.Wallpaper
import com.isanechek.wallpaper.data.repository.YaRepository
import com.isanechek.wallpaper.utils._drawable
import com.isanechek.wallpaper.utils._id
import com.isanechek.wallpaper.utils._layout
import com.isanechek.wallpaper.utils._string
import com.isanechek.wallpaper.utils.extensions.hide
import com.isanechek.wallpaper.utils.extensions.isApi
import com.isanechek.wallpaper.utils.extensions.marshmallow
import com.isanechek.wallpaper.utils.extensions.onClick
import com.isanechek.wallpaper.utils.glide.GlideApp
import com.isanechek.wallpaper.view.base.BaseFragment
import com.isanechek.wallpaper.view.widgets.AnimatedImageView
import com.isanechek.wallpaper.view.widgets.AnimatedTextView
import com.isanechek.wallpaper.view.widgets.DragLayout
import com.yandex.mobile.ads.AdEventListener
import com.yandex.mobile.ads.AdRequest
import com.yandex.mobile.ads.AdSize
import com.yandex.mobile.ads.AdView
import kotlinx.android.synthetic.main.details_screen_layout.*
import org.koin.android.ext.android.inject
import java.io.File

/**
 * Created by isanechek on 4/28/18.
 */
class DetailsFragment : BaseFragment(), DragLayout.StateVisibilityControlContainer {

    // view's
    private lateinit var cover: PhotoView
    private lateinit var photoContainer: FrameLayout
    private lateinit var container: DragLayout
    private lateinit var installBtn: Button
    private lateinit var controlContainer: FrameLayout

    private lateinit var swipeTv: AnimatedTextView
    private lateinit var swipeIv: AnimatedImageView

    // ads
    private lateinit var adView: AdView
    private lateinit var adRequest: AdRequest

    private val repository by inject<YaRepository>()

    private var wallpaper: Wallpaper? = null
        get() = arguments?.getParcelable<Wallpaper>(ARGS)

    override fun layoutResId(): Int = _layout.details_screen_layout

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        container = view.findViewById(_id.drag_layout)
        container.setVisibilityStateListener(this)
        cover = container.findViewById(_id.details_wallpaper)
        photoContainer = container.findViewById(_id.details_photo_container)

        swipeIv = container.findViewById(_id.details_swipe_iv)
        swipeTv = container.findViewById(_id.details_swipe_tv)

        controlContainer = container.findViewById(_id.details_control_container)
        installBtn = container.findViewById(_id.details_install_button)
        adView = container.findViewById(_id.details_screen_ads_widget)


    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        GlideApp.with(activity).load(wallpaper?.preview).into(cover)
        initAds()
        initObserver()

        installBtn.onClick {
            when {
                isApi(marshmallow) -> if (isCheckPermission()) {
                    startDownloadAction()
                }
                else -> startDownloadAction()
            }
        }
    }

    override fun state(visibility: Boolean) {
        if (!visibility) {
            please {
                animate(controlContainer) toBe {
                    alpha(1f)
                }

                animate(installBtn) toBe {
                    visible()
                    topOfItsParent()
                }

                animate(adView) toBe {
                    visible()
                    bottomOfItsParent()
                }
            }.thenCouldYou(duration = 150) {
                animate(swipeTv) toBe {

                }
                swipeTv.setAnimatedText(getString(_string.detailt_swipe_down_title), 250)
            }.thenCouldYou(duration = 150) {
                animate(swipeIv) toBe {
                    swipeIv.setAnimatedImage(_drawable.ic_expand_less_white_24dp)
                }
            }.start()
        } else {
            please {
                animate(adView) toBe {
                    outOfScreen(Gravity.BOTTOM)
                    invisible()
                }
                animate(installBtn) toBe {
                    outOfScreen(Gravity.TOP)
                    invisible()
                }
            }.thenCouldYou {
                animate(controlContainer) toBe {
                    alpha(0f)
                }
            }.thenCouldYou(duration = 150) {
                animate(swipeTv) toBe {
                    swipeTv.setAnimatedText(getString(_string.details_swipe_up_title), 250)
                }
            }.thenCouldYou(duration = 150) {
                animate(swipeIv) toBe {
                    swipeIv.setAnimatedImage(_drawable.ic_expand_more_white_24dp)
                }
            }.start()
        }
    }

    // permissons
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (permissions[0] == Manifest.permission.WRITE_EXTERNAL_STORAGE
                && permissions[0] == Manifest.permission.READ_EXTERNAL_STORAGE
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                startDownloadAction()
            }
        }
    }

    private fun isCheckPermission(): Boolean {
        var permissionState = true
        if (ContextCompat.checkSelfPermission(
                context!!.applicationContext,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                activity, arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ), REQUEST_PERMISSION_CODE
            )
            permissionState = false
        } else if (ContextCompat.checkSelfPermission(
                context!!.applicationContext,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                activity, arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ), REQUEST_PERMISSION_CODE
            )
            permissionState = false
        }
        return permissionState
    }

    // observer
    private fun initObserver() {
        repository._data.observe(this, Observer { item ->
            if (item != null) {
                detailToolbarProgress.hide()
                if (item.fullCachePath?.isNotEmpty() == true) {
                    val uri = FileProvider.getUriForFile(
                        activity,
                        BuildConfig.APPLICATION_ID + ".provider",
                        File(item.fullCachePath)
                    )
                    val wm = WallpaperManager.getInstance(activity)
                    startActivity(wm.getCropAndSetWallpaperIntent(uri))
                    repository._data.postValue(null)
                }
            }
        })
    }

    // download
    private fun startDownloadAction() {
        if (wallpaper == null) {
            Toast.makeText(activity, "Ooopsss.", Toast.LENGTH_SHORT).show()
            return
        }
        DownloadService.startDownloads(activity, wallpaper!!)
    }

    // ads
    private fun initAds() {
        adView.blockId = "R-M-DEMO-320x50"
        adView.adSize = AdSize.BANNER_320x50

        adRequest = AdRequest
            .builder()
            .build()
        adView.adEventListener = loadAdsBannerListener
        adView.loadAd(adRequest)
    }

    private val loadAdsBannerListener = object : AdEventListener.SimpleAdEventListener() {
        override fun onAdLoaded() {
            super.onAdLoaded()
        }
    }

    companion object {
        private const val ARGS = "test.args"
        private const val REQUEST_PERMISSION_CODE = 303
        fun args(wall: Wallpaper) = Bundle().apply {
            putParcelable(ARGS, wall)
        }
    }
}