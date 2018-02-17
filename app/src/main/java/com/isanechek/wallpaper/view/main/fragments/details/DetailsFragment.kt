package com.isanechek.wallpaper.view.main.fragments.details

import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.bumptech.glide.Glide
import com.isanechek.wallpaper.data.DownloadService
import com.isanechek.wallpaper.data.database.Wallpaper
import com.isanechek.wallpaper.data.network.Status
import com.isanechek.wallpaper.utils._layout
import com.isanechek.wallpaper.utils.extensions.emptyString
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
    private lateinit var viewModel: DetailsViewModel
    private var wall: Wallpaper? = null

    override fun layoutResId(): Int = _layout.details_fragment_layout2

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
    }

    private fun initObserver() {

        DownloadService.startDownloads(activity, wall!!)
        viewModel.getWallpaper(wall!!.title).observe(this, Observer<Wallpaper> {
            logger("RESULT ${it?.fullCachePath}")
            if (it != null && !it.fullCachePath.isNullOrEmpty()) {
                logger("$TAG point")
                detailsInstallButton.apply {
                    isEnabled = btnEnabled
                    onClick {
                        installWallpaper(Uri.parse(it.fullCachePath))
                    }
                }
            } else {
                logger("$TAG point pony")
                DownloadService.startDownloads(activity, wall!!)
            }
        })

        viewModel.status.observe(this, Observer { response ->
            if (response != null) {
                if (response.status == Status.LOADING) {
                    // show progress
                    logger("$TAG load url")
                } else if (response.status == Status.SUCCESS) {
                    // download bitmap to cache
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

    }

    private fun installWallpaper(path: Uri) {
        Intent(Intent.ACTION_ATTACH_DATA).run {
            flags = Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
            addCategory(Intent.CATEGORY_DEFAULT)
            setDataAndType(path, "image/jpeg")
            putExtra("mimeType", "image/jpeg")
            startActivity(Intent.createChooser(this, "Install Wallpaper"))
        }
    }

    companion object {
        private const val TAG = "DetailsFragment"
        fun arg(wallpaper: Wallpaper) = Bundle().apply { putParcelable("wall", wallpaper) }
    }
}