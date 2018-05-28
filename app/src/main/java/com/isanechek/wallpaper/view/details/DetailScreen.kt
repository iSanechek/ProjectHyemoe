package com.isanechek.wallpaper.view.details

import android.Manifest
import android.app.WallpaperManager
import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.AppCompatButton
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.github.chrisbanes.photoview.PhotoView
import com.github.florent37.kotlin.pleaseanimate.please
import com.isanechek.wallpaper.BuildConfig
import com.isanechek.wallpaper.R
import com.isanechek.wallpaper.data.DownloadService
import com.isanechek.wallpaper.data.database.Wallpaper
import com.isanechek.wallpaper.utils._id
import com.isanechek.wallpaper.utils._layout
import com.isanechek.wallpaper.utils._string
import com.isanechek.wallpaper.utils.extensions.*
import com.isanechek.wallpaper.utils.glide.GlideApp
import com.isanechek.wallpaper.utils.glide.palette.BitmapPalette
import com.isanechek.wallpaper.utils.glide.palette.GlidePalette
import com.isanechek.wallpaper.view.widgets.PullBackLayout
import eightbitlab.com.blurview.BlurView
import eightbitlab.com.blurview.RenderScriptBlur
import org.koin.android.ext.android.inject
import java.io.File

private const val ARGS = "test.args"
private const val REQUEST_PERMISSION_CODE = 303

class DetailScreen : AppCompatActivity() {

    private val pullback: PullBackLayout by lazy { findViewById<PullBackLayout>(_id.detail_pull_back_container) }
    private val cover: PhotoView by lazy { findViewById<PhotoView>(_id.detail_screen_photo_view) }
    private val blurBg: BlurView by lazy { findViewById<BlurView>(_id.detail_screen_blur_bg) }
    private val installBtn: AppCompatButton by lazy { findViewById<AppCompatButton>(_id.detail_screen_install_btn) }
    private val buttonBg: View by lazy { findViewById<View>(_id.detail_screen_btn_bg_view) }
    private val progress: ProgressBar by lazy { findViewById<ProgressBar>(_id.detail_screen_horizontal_progress) }
    private val rootView: ConstraintLayout by lazy { findViewById<ConstraintLayout>(_id.detail_screen_root) }

    private val viewModel: DetailScreenViewModel by inject()

    private var wallpaper: Wallpaper? = null
        get() = intent?.getParcelableExtra(ARGS)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (isApi(lollipop)) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }
        overridePendingTransition(R.anim.fade, 0)
        setContentView(_layout.detail_pullback_container)
        setupPullback()
        setupObserver()
        val url = wallpaper?.preview?: emptyString

        GlideApp.with(this)
                .load(url)
                .listener(object: RequestListener<Drawable> {
                    override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean): Boolean {
                        return false
                    }

                    override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean): Boolean {
                        blurBg.setupWith(rootView)
                                .windowBackground(resource!!)
                                .blurAlgorithm(RenderScriptBlur(this@DetailScreen))
                                .blurRadius(25f)
                                .setHasFixedTransformationMatrix(true)
                        return false
                    }

                }).into(cover)

        installBtn.onClick {
            if (progress.visibility == View.INVISIBLE) {
                progress.visibility = View.VISIBLE
            }

            when {
                isApi(marshmallow) -> if (isCheckPermission()) {
                    startDownloadAction()
                }
                else -> startDownloadAction()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (permissions[0] == Manifest.permission.WRITE_EXTERNAL_STORAGE
                    && permissions[0] == Manifest.permission.READ_EXTERNAL_STORAGE
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                startDownloadAction()
            }
        }
    }

    private fun setupPullback() {
        pullback.setCallback(object: PullBackLayout.Callback {
            override fun onPullStart() {}

            override fun onPull(direction: Int, progress: Float) {}

            override fun onPullCancel(direction: Int) {}

            override fun onPullComplete(direction: Int) {
                finishAfterTransition()
                if (direction == PullBackLayout.DIRECTION_DOWN) {
                    overridePendingTransition(R.anim.fade, R.anim.activity_slide_down)
                } else {
                    overridePendingTransition(R.anim.fade, R.anim.activity_slide_up)
                }
            }
        })
    }

    private fun isCheckPermission(): Boolean {
        var permissionState = true
        if (ContextCompat.checkSelfPermission(
                        this@DetailScreen.applicationContext,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                    this@DetailScreen, arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            ), REQUEST_PERMISSION_CODE
            )
            permissionState = false
        } else if (ContextCompat.checkSelfPermission(
                        this@DetailScreen.applicationContext,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                    this@DetailScreen, arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            ), REQUEST_PERMISSION_CODE
            )
            permissionState = false
        }
        return permissionState
    }

    private fun startDownloadAction() {
        if (wallpaper == null) {
            Toast.makeText(this, "Ooopsss.", Toast.LENGTH_SHORT).show()
            return
        }

        DownloadService.startDownloads(this, wallpaper!!)
    }


    private fun setupObserver() {
        viewModel.data.observe(this, Observer { wallpaper ->
            if (wallpaper == null) return@Observer
            if (progress.visibility == View.VISIBLE) progress.visibility = View.INVISIBLE
            if (wallpaper.fullCachePath?.isNotEmpty() == true) {
                val uri = FileProvider.getUriForFile(
                            this@DetailScreen,
                            BuildConfig.APPLICATION_ID + ".provider",
                            File(wallpaper.fullCachePath)
                    )

                val wm = WallpaperManager.getInstance(this@DetailScreen)
                startActivity(wm.getCropAndSetWallpaperIntent(uri))
                viewModel.clearData()
            } else toast(_string.load_wallpaper_error_toast_title)
        })
    }

    companion object {
        fun create(context: Context, wallpaper: Wallpaper) {
            Intent(context, DetailScreen::class.java).run {
                putExtra(ARGS, wallpaper)
                context.startActivity(this)
            }
        }
    }
}