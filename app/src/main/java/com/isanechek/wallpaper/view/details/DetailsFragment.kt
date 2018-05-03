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
import android.view.ViewGroup
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
import com.isanechek.wallpaper.utils.extensions.*
import com.isanechek.wallpaper.utils.glide.GlideApp
import com.isanechek.wallpaper.view.base.BaseFragment
import com.isanechek.wallpaper.view.widgets.AnimatedImageView
import com.isanechek.wallpaper.view.widgets.AnimatedTextView
import com.isanechek.wallpaper.view.widgets.DragLayout
import com.vlad1m1r.lemniscate.BernoullisProgressView
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
    private lateinit var progress: BernoullisProgressView

    private val repository by inject<YaRepository>()

    private var wallpaper: Wallpaper? = null
        get() = arguments?.getParcelable(ARGS)

    override fun layoutResId(): Int = _layout.details_screen_layout

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        container = view.findViewById(_id.drag_layout)
        container.setVisibilityStateListener(this)
        cover = container.findViewById(_id.details_wallpaper)
        photoContainer = container.findViewById(_id.details_photo_container)

        swipeIv = container.findViewById(_id.details_swipe_iv)
        swipeTv = container.findViewById(_id.details_swipe_tv)
        progress = container.findViewById(_id.details_screen_progress)

        controlContainer = container.findViewById(_id.details_control_container)
        installBtn = container.findViewById(_id.details_install_button)

        please {
            animate(installBtn) toBe {
                outOfScreen(Gravity.BOTTOM)
            }
            animate(progress) toBe {
                invisible()
            }
        }.now()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        GlideApp.with(activity).load(wallpaper?.preview).into(cover)
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

            }.thenCouldYou(duration = 10) {
                animate(installBtn) toBe {
                    visible()
                    topOfItsParent()
                }
            }.start()
        } else {
            please {
                animate(installBtn) toBe {
                    outOfScreen(Gravity.BOTTOM)
                    invisible()
                }
            }.thenCouldYou(duration = 10) {
                animate(controlContainer) toBe {
                    alpha(0f)
                }
            }.start()
        }
    }

    override fun getTitle(): String = ""

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
                please {
                    animate(progress) toBe {
                        invisible()
                    }
                }.thenCouldYou(duration = 10) {
                    animate(installBtn) toBe {
                        topOfItsParent()
                    }
                }.start()
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

        please {
            animate(installBtn) toBe {
                outOfScreen(Gravity.TOP)
            }
        }.thenCouldYou(duration = 10) {
            animate(progress) toBe {
                visible()
            }
        }.start()

        DownloadService.startDownloads(activity, wallpaper!!)
    }


    companion object {
        private const val ARGS = "test.args"
        private const val REQUEST_PERMISSION_CODE = 303
        fun args(wall: Wallpaper) = Bundle().apply {
            putParcelable(ARGS, wall)
        }
    }
}