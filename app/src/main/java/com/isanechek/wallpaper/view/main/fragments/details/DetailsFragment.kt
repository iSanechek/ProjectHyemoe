package com.isanechek.wallpaper.view.main.fragments.details

import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.bumptech.glide.Glide
import com.isanechek.wallpaper.data.database.Wallpaper
import com.isanechek.wallpaper.data.network.Status
import com.isanechek.wallpaper.utils.Const
import com.isanechek.wallpaper.utils.L
import com.isanechek.wallpaper.utils.extensions.emptyString
import com.isanechek.wallpaper.utils.extensions.fileIsExists
import com.isanechek.wallpaper.utils.extensions.onClick
import com.isanechek.wallpaper.utils.logger
import com.isanechek.wallpaper.view.base.BaseFragment
import kotlinx.android.synthetic.main.details_fragment_layout2.*
import org.koin.android.architecture.ext.getViewModel


/**
 * Created by isanechek on 11/6/17.
 */
class DetailsFragment : BaseFragment() {
    private var btnEnabled = true
    private lateinit var _title: String
    private lateinit var _url: String
    private var wall: Wallpaper? = null
//    private val viewModel by nonSafeLazy { ViewModelProviders.of(this)[DetailsViewModel::class.java] }
    private lateinit var viewModel: DetailsViewModel

    override fun layoutResId(): Int = L.details_fragment_layout2

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        wall = arguments?.getParcelable("wall")
        _title = wall?.title ?: emptyString
    }

    override fun getTitle(): String = _title

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = getViewModel()

        initObserver()

        val path = wall?.preview
        Glide.with(this)
                .asBitmap()
                .load(path)
                .into(detailsImgCover)

         detailsInstallButton.apply {
             isEnabled = btnEnabled
             onClick { clickAction() }
         }
    }

    private fun clickAction() {
        if (_title.isNotEmpty()) {
            viewModel.getWallObj(_title).observe(this, Observer { item ->
                if (item != null) {
                    val localPath = item.cacheKey ?: emptyString
                    if (localPath.isNotEmpty()) {
                        if (fileIsExists(localPath)) {
                            installWallpaper(Uri.parse(localPath))
                        } else actionGetUrl()
                    } else actionGetUrl()
                }
            })
        }
    }

    private fun actionGetUrl() {
        if (wall == null) {
            // show error message
        } else {
            wall?.let { viewModel.getDownloadUrl(it) }
        }

    }

    private fun initObserver() {
        viewModel.status.observe(this, Observer { response ->
            if (response != null) {
                if (response.status == Status.LOADING) {
                    // show progress
                    logger("$TAG load url")
                } else if (response.status == Status.SUCCESS) {
                    // download bitmap to cache
                    val bitmapUrl = response.data as String
                    viewModel.getBitmapPath(bitmapUrl, activity)
                    logger("$TAG url download ${response.data}")
                } else if (response.status == Status.ERROR) {
                    // show error message
                    logger("$TAG Load url error")
                } else {
                    // show error message
                    logger("$TAG Load url WTF")
                }
            }
        })

        viewModel.uri.observe(this, Observer { result ->
            if (result != null) {
                if (result.status == Status.LOADING) {
                    logger("$TAG Loading")
                    if (result.message ==  Const.REQUEST_BITMAP) {
                        logger("$TAG Download bitmap")
                    } else if (result.message == Const.SAVE_BITMAP) {
                        logger("$TAG Save bitmap")
                    }
                } else if (result.status == Status.SUCCESS) {
                    if (result.data != null) {
                        logger("URI File ${result.data}")
                        installWallpaper(result.data)
                        wall?.cacheKey = result.data.toString()
                        wall?.let { viewModel.updateWallpaper(it) }
                    }
                } else if (result.status == Status.ERROR) {
                    logger("$TAG Error ${result.message}")
                }
            }
        })
    }

    private fun installWallpaper(path: Uri) {
        val intent = Intent(Intent.ACTION_ATTACH_DATA)
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        intent.addCategory(Intent.CATEGORY_DEFAULT)
        intent.setDataAndType(path, "image/jpeg")
        intent.putExtra("mimeType", "image/jpeg")
        startActivity(Intent.createChooser(intent, "Install"))
    }

    companion object {
        private const val TAG = "DetailsFragment"
        fun arg(wallpaper: Wallpaper) = Bundle().apply { putParcelable("wall", wallpaper) }
    }
}