package com.isanechek.wallpaper.view.details

import android.os.Bundle
import android.view.View
import com.github.chrisbanes.photoview.PhotoView
import com.isanechek.wallpaper.data.database.Wallpaper
import com.isanechek.wallpaper.utils._id
import com.isanechek.wallpaper.utils._layout
import com.isanechek.wallpaper.utils.glide.GlideApp
import com.isanechek.wallpaper.view.base.BaseFragment

/**
 * Created by isanechek on 4/28/18.
 */
class DetailsFragment : BaseFragment() {

    private lateinit var pv: PhotoView

    private var url: String? = ""
        get() = arguments?.getParcelable<Wallpaper>(ARGS)?.preview

    override fun layoutResId(): Int = _layout.test_details_screen_layout

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pv = view.findViewById(_id.test_pv)

        GlideApp.with(activity).load(url).into(pv)
    }

    companion object {
        private const val ARGS = "test.args"

        fun args(wall: Wallpaper) = Bundle().apply {
            putParcelable(ARGS, wall)
        }


        fun create(wallpaper: Wallpaper): DetailsFragment {
            val f = DetailsFragment()
            f.arguments = Bundle().apply {
                putParcelable(ARGS, wallpaper)
            }
            return f
        }

    }
}