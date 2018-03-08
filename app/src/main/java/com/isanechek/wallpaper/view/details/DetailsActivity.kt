package com.isanechek.wallpaper.view.details

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.app.WallpaperManager
import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.transition.Slide
import android.support.transition.Transition
import android.support.transition.TransitionManager
import android.support.transition.TransitionSet
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.view.View
import android.view.ViewAnimationUtils
import android.view.animation.AccelerateInterpolator
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.egoriku.corelib_kt.dsl.afterMeasured
import com.egoriku.corelib_kt.dsl.fromApi
import com.egoriku.corelib_kt.dsl.show
import com.isanechek.wallpaper.BuildConfig
import com.isanechek.wallpaper.data.DownloadService
import com.isanechek.wallpaper.data.database.Wallpaper
import com.isanechek.wallpaper.data.repository.YaRepository
import com.isanechek.wallpaper.utils._drawable
import com.isanechek.wallpaper.utils._id
import com.isanechek.wallpaper.utils._layout
import com.isanechek.wallpaper.utils.extensions.hide
import com.isanechek.wallpaper.utils.extensions.invisible
import com.isanechek.wallpaper.utils.extensions.onClick
import com.isanechek.wallpaper.utils.logger
import com.isanechek.wallpaper.view.widgets.DragLayout
import kotlinx.android.synthetic.main.details_activity_layout.*
import org.koin.android.ext.android.inject
import java.io.File

/**
 * Created by isanechek on 2/25/18.
 */
class DetailsActivity : AppCompatActivity() {

    private lateinit var container: DragLayout
    private lateinit var cover: ImageView
    private lateinit var installBtn: Button
    private lateinit var controlContainer: FrameLayout

    private val repository by inject<YaRepository>()
    private var wall: Wallpaper? = null

    private var revealX: Int = 0
    private var revealY: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.TRANSPARENT
        setContentView(_layout.details_activity_layout)
        container = findViewById(_id.drag_layout)
        cover = container.findViewById(_id.details_wallpaper)
        installBtn = container.findViewById(_id.details_install_button)
        controlContainer = container.findViewById(_id.details_control_container)

        container.setVisibilityStateListener(object: DragLayout.StateVisibilityControlContainer {
            override fun state(visibility: Boolean) = if (!visibility) {
                details_install_button.visibility = View.VISIBLE
                details_ads.visibility = View.VISIBLE
                details_swipe_iv.setAnimatedImage(_drawable.ic_expand_less_white_24dp)
                details_swipe_tv.setAnimatedText("swipe down", 250)
                logger("State show")
            } else {
                logger("State hide")
                details_install_button.hide()
                details_ads.hide()
                details_swipe_iv.setAnimatedImage(_drawable.ic_expand_more_white_24dp)
                details_swipe_tv.setAnimatedText("swipe up", 250)
            }
        })

        wall = intent.extras.getParcelable(DETAILS_ARGS) as Wallpaper

        fromApi(Build.VERSION_CODES.LOLLIPOP, true) {
            revealX = intent.getIntExtra(EXTRA_CIRCULAR_REVEAL_X, 0)
            revealY = intent.getIntExtra(EXTRA_CIRCULAR_REVEAL_Y, 0)
            details_root.afterMeasured {
                val finalRadius = (Math.max(details_root.width, details_root.height) * 1.1).toFloat()
                ViewAnimationUtils.createCircularReveal(details_root, revealX, revealY, 0f, finalRadius).apply {
                    duration = ANIMATION_DURATION
                    interpolator = AccelerateInterpolator()
                    details_root.show()
                    start()
                }
            }
        }
        Glide.with(this).load(wall?.preview).into(cover)

        TransitionManager.beginDelayedTransition(detailsCloseButtonContainer, Slide(Gravity.LEFT))

        detailArcView.onClick {
            TransitionManager.beginDelayedTransition(detailsCloseButtonContainer, Slide(Gravity.LEFT))
            unRevealActivity()
            finish()
        }

        details_install_button.onClick {
            if (isCheckPermission()) {
                startDownloadAction()
            }
        }

        repository._data.observe(this, Observer { item ->
            if (item != null) {
                detailToolbarProgress.hide()
                if (item.fullCachePath?.isNotEmpty() == true) {
                    logger("$TAG ${item.fullCachePath}")
                    val uri = FileProvider.getUriForFile(this,
                            BuildConfig.APPLICATION_ID + ".provider",
                            File(item.fullCachePath))

                    logger("Details uri ${uri.path}")
                    val wm = WallpaperManager.getInstance(this)
                    startActivity(wm.getCropAndSetWallpaperIntent(uri))
                }
            } else {
                logger("$TAG item null")
                detailToolbarProgress.hide()
            }
        })

    }

    override fun onBackPressed() {
        unRevealActivity()

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

    private fun unRevealActivity() {
        val finalRadius = (Math.max(details_root.width, details_root.height) * 1.1).toFloat()

        ViewAnimationUtils.createCircularReveal(details_root, revealX, revealY, finalRadius, 0f).apply {
            duration = ANIMATION_DURATION
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    details_root.invisible()
                    finish()
                }
            })
            start()
        }
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

    companion object {
        private const val TAG = "DetailsActivity"
        private const val EXTRA_CIRCULAR_REVEAL_X = "REVEAL_X"
        private const val EXTRA_CIRCULAR_REVEAL_Y = "REVEAL_Y"
        private const val ANIMATION_DURATION = 400L
        private const val DETAILS_ARGS = "details.args"
        private const val REQUEST_PERMISSION_CODE = 303
        fun start(context: Context, wallpaper: Wallpaper) {
            Intent(context, DetailsActivity::class.java).run {
                putExtra(DETAILS_ARGS, wallpaper)
                context.startActivity(this)
            }
        }

        fun startWithTransation(context: Activity, wallpaper: Wallpaper, view: View) {
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(context, view, "transition")
            val revealX = (view.x + view.width / 2).toInt()
            val revealY = (view.y + view.height / 2).toInt()
            ActivityCompat.startActivity(context,
                    Intent(context, DetailsActivity::class.java).apply {
                        putExtra(DETAILS_ARGS, wallpaper)
                        putExtra(EXTRA_CIRCULAR_REVEAL_X, revealX)
                        putExtra(EXTRA_CIRCULAR_REVEAL_Y, revealY)
                    },
                    options.toBundle())

        }
    }
}