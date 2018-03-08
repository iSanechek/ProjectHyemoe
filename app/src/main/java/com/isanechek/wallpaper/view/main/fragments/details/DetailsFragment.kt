package com.isanechek.wallpaper.view.main.fragments.details

import android.arch.lifecycle.Observer
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.isanechek.wallpaper.data.DownloadService
import com.isanechek.wallpaper.data.database.Wallpaper
import com.isanechek.wallpaper.data.network.Status
import com.isanechek.wallpaper.utils._id
import com.isanechek.wallpaper.utils._layout
import com.isanechek.wallpaper.utils.extensions.emptyString
import com.isanechek.wallpaper.utils.extensions.onClick
import com.isanechek.wallpaper.utils.logger
import com.isanechek.wallpaper.view.base.BaseFragment
import com.isanechek.wallpaper.view.widgets.DragLayout
import kotlinx.android.synthetic.main.details_activity_layout.*
import org.koin.android.architecture.ext.getViewModel


/**
 * Created by isanechek on 11/6/17.
 */
class DetailsFragment : BaseFragment() {
    private var btnEnabled = true
    private lateinit var _title: String
    private var wall: Wallpaper? = null

    private lateinit var viewModel: DetailsViewModel
    private lateinit var requestManager: RequestManager
    // view's
    private lateinit var container: DragLayout
    private lateinit var cover: ImageView
    private lateinit var installBtn: Button

    override fun layoutResId(): Int = _layout.details_activity_layout

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        wall = arguments?.getParcelable("wall")
        _title = wall?.title ?: emptyString
    }

    override fun getTitle(): String = _title

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        container = view.findViewById(_id.drag_layout)
        cover = container.findViewById(_id.details_wallpaper)
        installBtn = container.findViewById(_id.details_install_button)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = getViewModel()
        requestManager = Glide.with(this)

        detailArcView.onClick {
            navigator.goBack()
        }

        initLoadCover()
        initObserver()
    }

    private fun initLoadCover() {
        Glide.with(this).load(wall?.preview).into(cover)
    }

    private fun initObserver() {

        viewModel.getWallpaper(wall!!.title).observe(this, Observer<Wallpaper> {
            logger("RESULT ${it?.fullCachePath}")
            if (it != null && !it.fullCachePath.isNullOrEmpty()) {
                logger("$TAG point")
//                detailsInstallButton.apply {
//                    isEnabled = btnEnabled
//                    onClick {
//                        installWallpaper(Uri.parse(it.fullCachePath))
//                    }
//                }
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


    companion object {
        private const val TAG = "DetailsFragment"
        fun arg(wallpaper: Wallpaper) = Bundle().apply { putParcelable("wall", wallpaper) }
    }
}