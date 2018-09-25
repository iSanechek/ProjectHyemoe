package com.isanechek.wallpaper.ui.details

import _id
import _layout
import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ProgressBar
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.github.chrisbanes.photoview.PhotoView
import com.isanechek.common.models.Wallpaper
import com.isanechek.extensions.*
import com.isanechek.wallpaper.R
import com.isanechek.wallpaper.ui.base.BaseActivity
import com.isanechek.wallpaper.ui.widgets.PullBackLayout
import com.isanechek.wallpaper.utils.network.Connection
import com.squareup.picasso.Picasso
import com.yandex.metrica.YandexMetrica
import net.steamcrafted.loadtoast.LoadToast
import org.json.JSONObject
import org.koin.android.ext.android.inject

private const val ARGS = "detail.args"
private const val REQUEST_PERMISSION_CODE = 303

class DetailScreen : BaseActivity() {

    private val pullback: PullBackLayout by lazy { findViewById<PullBackLayout>(_id.detail_pull_back_container) }
    private val cover: PhotoView by lazy { findViewById<PhotoView>(_id.detail_screen_photo_view) }
    private val installBtn: AppCompatButton by lazy { findViewById<AppCompatButton>(_id.detail_screen_install_btn) }
    private val buttonBg: View by lazy { findViewById<View>(_id.detail_screen_btn_bg_view) }
    private val progress: ProgressBar by lazy { findViewById<ProgressBar>(_id.detail_screen_horizontal_progress) }
    private val rootView: ConstraintLayout by lazy { findViewById<ConstraintLayout>(_id.detail_screen_root) }
    private val closeBtn: ImageButton by lazy { findViewById<ImageButton>(_id.detail_screen_close_btn) }

    private val viewModel: DetailScreenViewModel by inject()
    private lateinit var toastMsg: LoadToast

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
        val url = wallpaper?.previewUrl?: emptyString
        Picasso.get().load(url).into(cover)

        toastMsg = LoadToast(this)

        installBtn.onClick {
//            if (progress.visibility == View.INVISIBLE) {
//                progress.visibility = View.VISIBLE
//            }

            when {
                isApi(marshmallow) -> if (isCheckPermission()) {
                    startDownloadAction()
                }
                else -> startDownloadAction()
            }
        }

        closeBtn.onClick {
            if (isApi(lollipop)) {
                finishAfterTransition()
            } else finish()
            overridePendingTransition(R.anim.fade, R.anim.activity_slide_down)
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

    override fun onFragmentChanged(currentTag: String, currentFragment: androidx.fragment.app.Fragment) {
        //To change body of created functions use File | Settings | File Templates.
    }

    override fun setupWaterfall(recycler: RecyclerView) {

    }

    private fun setupPullback() {
        pullback.setCallback(object: PullBackLayout.Callback {
            override fun onPullStart() {}

            override fun onPull(direction: Int, progress: Float) {}

            override fun onPullCancel(direction: Int) {}

            override fun onPullComplete(direction: Int) {
                if (isApi(lollipop)) {
                    finishAfterTransition()
                } else finish()
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
        toastMsg.setText("Downloading...")
                .setBackgroundColor(Color.parseColor("#6339ac"))
                .setTextColor(Color.parseColor("#ffffff"))
                .setProgressColor(Color.parseColor("#ffffff"))
                .setTranslationY(100)
                .show()


        if (wallpaper == null) {
            toastMsg.error()
            return
        }
        val jo = JSONObject()
        jo.put("title", wallpaper!!.title)
        jo.put("size", wallpaper!!.size)
        jo.put("path", wallpaper!!.publicPath)
        YandexMetrica.reportEvent("install wallpaper", jo.toString())
        viewModel.loadWallpaper(wallpaper!!)

    }


    private fun setupObserver() {

        connection.observe(this, Observer { conn ->
            conn ?: return@Observer
            when(conn.type) {
                Connection.WIFI -> {
                    disableBtn(false)
                }
                Connection.MOBILE -> {
                    disableBtn(false)
                }
                Connection.OFFLINE -> {
                    disableBtn()
                }
            }
        })
    }

    private fun disableBtn(disable: Boolean = true) {
        if (!disable) installBtn.isEnabled = true else
            if (installBtn.isEnabled) installBtn.isEnabled = false
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