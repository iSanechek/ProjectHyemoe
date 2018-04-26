package com.isanechek.wallpaper.view.details

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.WallpaperManager
import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.ViewTreeObserver
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.github.florent37.kotlin.pleaseanimate.please
import com.isanechek.wallpaper.BuildConfig
import com.isanechek.wallpaper.data.DownloadService
import com.isanechek.wallpaper.data.database.Wallpaper
import com.isanechek.wallpaper.data.repository.YaRepository
import com.isanechek.wallpaper.utils.*
import com.isanechek.wallpaper.utils.extensions.*
import com.isanechek.wallpaper.utils.glide.GlideApp
import com.isanechek.wallpaper.view.widgets.AnimatedImageView
import com.isanechek.wallpaper.view.widgets.AnimatedTextView
import com.isanechek.wallpaper.view.widgets.ArcView
import com.isanechek.wallpaper.view.widgets.DragLayout
import com.yandex.mobile.ads.AdEventListener
import com.yandex.mobile.ads.AdRequest
import com.yandex.mobile.ads.AdSize
import com.yandex.mobile.ads.AdView
import kotlinx.android.synthetic.main.details_screen_layout.*
import org.koin.android.ext.android.inject
import java.io.File

/**
 * Created by isanechek on 2/25/18.
 */
class DetailsActivity : AppCompatActivity(), DragLayout.StateVisibilityControlContainer {

    // view's
    private lateinit var cover: ImageView
    private lateinit var container: DragLayout
    private lateinit var installBtn: Button
    private lateinit var controlContainer: FrameLayout

    private lateinit var swipeTv: AnimatedTextView
    private lateinit var swipeIv: AnimatedImageView

    private lateinit var arcView: ArcView
    private lateinit var closeBtn: AnimatedImageView

    // ads
    private lateinit var adView: AdView
    private lateinit var adRequest: AdRequest

    private val repository by inject<YaRepository>()
    private var wall: Wallpaper? = null

    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fromApi(lollipop) {
            window.statusBarColor = Color.TRANSPARENT
        }

        setContentView(_layout.details_screen_layout)

        container = findViewById(_id.drag_layout)
        container.setVisibilityStateListener(this)

        arcView = findViewById(_id.detailArcView)
        closeBtn = findViewById(_id.detailArcImage)
        swipeIv = container.findViewById(_id.details_swipe_iv)
        swipeTv = container.findViewById(_id.details_swipe_tv)

        please(duration = 250) {
            animate(arcView) toBe {
                alpha(1f)
            }

            animate(closeBtn) toBe {
                alpha(1f)
                closeBtn.setAnimatedImage(_drawable.ic_close_white_24dp, 250)
            }
        }.thenCouldYou(duration = 150) {
            animate(swipeTv) toBe {
                alpha(1f)
                swipeTv.setAnimatedText(getString(_string.detailt_swipe_down_title), 250)
            }
        }.thenCouldYou(duration = 150) {
            animate(swipeIv) toBe {
                alpha(1f)
                swipeIv.setAnimatedImage(_drawable.ic_expand_less_white_24dp)
            }
        }

        cover = container.findViewById(_id.details_wallpaper)
        initLoadImg()

        controlContainer = container.findViewById(_id.details_control_container)
        installBtn = container.findViewById(_id.details_install_button)
        installBtn.onClick {
            if (isApi(marshmallow)) {
                if (isCheckPermission()) {
                    startDownloadAction()
                }
            } else startDownloadAction()
        }
        adView = container.findViewById(_id.details_screen_ads_widget)
        please {
            animate(controlContainer) toBe {
                invisible()
            }

            animate(installBtn) toBe {
                invisible()
            }

            animate(adView) toBe {
                invisible()
            }
        }.now()

        closeBtn.onClick {
            please(duration = 250) {
                animate(closeBtn) toBe {
                    invisible()
                }
                animate(arcView) toBe {
                    invisible()
                }
            }.start()
            closeActivity()
        }

        wall = intent.extras.getParcelable(DETAILS_ARGS) as Wallpaper

        repository._data.observe(this, Observer { item ->
            if (item != null) {
                detailToolbarProgress.hide()
                if (item.fullCachePath?.isNotEmpty() == true) {
                    val uri = FileProvider.getUriForFile(this,
                            BuildConfig.APPLICATION_ID + ".provider",
                            File(item.fullCachePath))
                    val wm = WallpaperManager.getInstance(this)
                    startActivity(wm.getCropAndSetWallpaperIntent(uri))
                    repository._data.postValue(null)
                }
            } else {
                logger("$TAG item null")
                detailToolbarProgress.hide()
            }
        })

        initAds()
    }

    override fun state(visibility: Boolean) {
        if (!visibility) {
            please {
                animate(controlContainer) toBe {
                    visible()
                }

                animate(installBtn) toBe {
                    visible()
                }

                animate(adView) toBe {
                    visible()
                }
            }.thenCouldYou(duration = 150) {
                animate(swipeTv) toBe {
                    swipeTv.setAnimatedText(getString(_string.detailt_swipe_down_title), 250)
                }
            }.thenCouldYou(duration = 150) {
                animate(swipeIv) toBe {
                    swipeIv.setAnimatedImage(_drawable.ic_expand_less_white_24dp)
                }
            }.start()
        } else {
            please {
                animate(controlContainer) toBe {
                    invisible()
                }

                animate(installBtn) toBe {
                    invisible()
                }

                animate(adView) toBe {
                    invisible()
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

    private fun initLoadImg() {

        if (isApi(lollipop)) {
            supportPostponeEnterTransition()
            GlideApp.with(this)
                    .load(wall?.preview)
                    .centerCrop()
                    .dontAnimate()
                    .listener(object: RequestListener<Drawable> {
                        override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                            supportStartPostponedEnterTransition()
                            return false
                        }

                        override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                            cover.viewTreeObserver.addOnPreDrawListener(object: ViewTreeObserver.OnPreDrawListener {
                                override fun onPreDraw(): Boolean {
                                    cover.viewTreeObserver.removeOnPreDrawListener(this)
                                    supportStartPostponedEnterTransition()
                                    return false
                                }
                            })
                            return false
                        }
                    })
                    .into(cover)
        } else GlideApp.with(this).load(wall?.preview).into(cover)

    }

    override fun onBackPressed() {
        super.onBackPressed()
        closeActivity()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == DetailsActivity.REQUEST_PERMISSION_CODE) {
            if (permissions[0] == Manifest.permission.WRITE_EXTERNAL_STORAGE
                    && permissions[0] == Manifest.permission.READ_EXTERNAL_STORAGE
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startDownloadAction()
            }
        }
    }

    @SuppressLint("NewApi")
    private fun closeActivity() {
        if (isApi(lollipop)) {
            finishAfterTransition()
        } else finish()
    }

    private fun isCheckPermission(): Boolean {
        var permissionState = true
        if (ContextCompat.checkSelfPermission(applicationContext, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this@DetailsActivity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE), DetailsActivity.REQUEST_PERMISSION_CODE)
            permissionState = false
        } else if (ContextCompat.checkSelfPermission(applicationContext, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this@DetailsActivity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE), DetailsActivity.REQUEST_PERMISSION_CODE)
            permissionState = false
        }
        return permissionState
    }

    private fun startDownloadAction() {
        detailToolbarProgress.show()
        if (wall == null) {
            Toast.makeText(this, "Ooopsss.", Toast.LENGTH_SHORT).show()
            return
        }
        DownloadService.startDownloads(this@DetailsActivity, wall!!)
    }

    private fun initAds() {
        adView.blockId = "R-M-DEMO-320x50"
        adView.adSize = AdSize.BANNER_320x50

        adRequest = AdRequest
                .builder()
                .build()
        adView.adEventListener = loadAdsBannerListener
        adView.loadAd(adRequest)
    }

    private val loadAdsBannerListener = object: AdEventListener.SimpleAdEventListener() {
        override fun onAdLoaded() {
            super.onAdLoaded()
        }
    }

    companion object {
        private const val TAG = "DetailsActivity"
        private const val ANIMATION_DURATION = 400L
        private const val DETAILS_ARGS = "details.args"
        private const val REQUEST_PERMISSION_CODE = 303
        fun start(context: Context, wallpaper: Wallpaper) {
            Intent(context, DetailsActivity::class.java).run {
                putExtra(DETAILS_ARGS, wallpaper)
                context.startActivity(this)
            }
        }

        fun startWithAnimation(context: Activity, wallpaper: Wallpaper, view: View): Unit =
                if (isApi(lollipop)) {
                    val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                            context, view, context.getString(_string.wall_image))
                    ActivityCompat.startActivity(context,
                            Intent(context, DetailsActivity::class.java).apply {
                                putExtra(DETAILS_ARGS, wallpaper)
                            },
                            options.toBundle())
                } else {
                    Intent(context, DetailsActivity::class.java).run {
                        putExtra(DETAILS_ARGS, wallpaper)
                        context.startActivity(this)
                    }
                }
    }
}