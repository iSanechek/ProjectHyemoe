package com.isanechek.wallpaper.ui.category

import android.os.Bundle
import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.isanechek.common.DebugUtils
import com.isanechek.common.models.Category
import com.isanechek.repository.Status.*
import com.isanechek.extensions.extraWithKey
import com.isanechek.extensions.hide
import com.isanechek.extensions.onClick
import com.isanechek.extensions.show
import com.isanechek.wallpaper.ui.base.BaseFragment
import com.isanechek.wallpaper.ui.timeline.TimelineFragment
import com.isanechek.wallpaper.ui.widgets.navigation.NavigationId
import com.list.rados.fastlist.bind
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import com.yandex.metrica.YandexMetrica
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator
import kotlinx.android.synthetic.main.category_list_item_layout.view.*
import kotlinx.android.synthetic.main.category_screen_layout.*
import kotlinx.android.synthetic.main.progress_error_layout.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.lang.Exception

/**
 * Created by isanechek on 9/26/17.
 */

private const val TAG = "CategoryFragment"

class CategoryFragment : BaseFragment() {

    private val debug by inject<DebugUtils>()
    private val viewModel: CategoryViewModel by viewModel()
    private val diffCallback = object : DiffUtil.ItemCallback<Category>() {
        override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean =
                oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean =
                oldItem == newItem

    }

    private val key: String
        get() = this extraWithKey "key.category"

    private val path: String
        get() = this extraWithKey "path.category"

    override fun layoutResId(): Int = _layout.category_screen_layout

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progress_view_progress.progressColor = _color.my_primary_dark_color

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupTestObserver()
        debug.log("$TAG hya")
        viewModel.loadData(key, path)
    }

    override fun getTitle(): String = NavigationId.CATEGORY.name

    private fun setupTestObserver() {
        viewModel.data.observe(this@CategoryFragment, Observer {
            it ?: return@Observer
            debug.log("$TAG data ${it.isEmpty()}")
            with(category_screen_list) {
                setHasFixedSize(true)
                val animator = SlideInUpAnimator(OvershootInterpolator(1f))
                animator.addDuration = 350
                itemAnimator = animator
                bind(diffCallback, _layout.category_list_item_layout) { category: Category ->
                    debug.log("$TAG item ${category.title}")
                    cat_item_root.onClick {
                        YandexMetrica.reportEvent("open category", category.title)
                        goTo<TimelineFragment>(
                                keepState = false,
                                withCustomAnimation = true,
                                arg = bundleOf("timeline.screen.title" to category.title,
                                        "timeline.screen.wall.key" to category.publicKey,
                                        "timeline.screen.wall.path" to category.publicPath)
                        )
                    }
                    cat_item_title_tv.text = category.title
                    Picasso.get()
                            .load(category.previewUrl)
                            .transform(RoundedCornersTransformation(8, 0))
                            .into(cat_item_cover_iv, object: Callback {
                                override fun onSuccess() {
                                    debug.log("$TAG ${category.title} done")
                                }

                                override fun onError(e: Exception?) {
                                    debug.log("$TAG ${category.title} fail ${e?.message}")
                                }

                            })
                }.layoutManager(GridLayoutManager(activity, 2, GridLayoutManager.HORIZONTAL, false))
                        .submitList(it)
            }
        })

        viewModel.networkState.observe(this@CategoryFragment, Observer {
            it ?: return@Observer
            when(it.status) {
                FAILED -> {
                    progress_view_progress.stop()
                    progress_view_container.hide()
                }

                RUNNING -> {

                }

                SUCCESS -> {
                    progress_view_progress.stop()
                    progress_view_container.hide()
                }

                BAD_REQUEST -> TODO()
                NOT_FIND -> TODO()
                INITIAL -> {
                    progress_view_container.show()
                    progress_view_progress.start()
                }
            }
        })
    }
}