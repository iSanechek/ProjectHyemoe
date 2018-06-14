package com.isanechek.wallpaper.view.main.fragments.category

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v7.widget.AppCompatButton
import android.support.v7.widget.AppCompatTextView
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.animation.OvershootInterpolator
import com.isanechek.wallpaper.R
import com.isanechek.wallpaper.data.network.RequestStrategy
import com.isanechek.wallpaper.data.network.Status
import com.isanechek.wallpaper.utils.*
import com.isanechek.wallpaper.utils.extensions.hide
import com.isanechek.wallpaper.utils.extensions.onClick
import com.isanechek.wallpaper.utils.extensions.show
import com.isanechek.wallpaper.utils.network.Connection
import com.isanechek.wallpaper.view.base.BaseFragment
import com.isanechek.wallpaper.view.main.fragments.timeline.TimelineFragment
import com.isanechek.wallpaper.view.widgets.navigation.NavigationId
import com.vlad1m1r.lemniscate.BernoullisProgressView
import com.yandex.metrica.YandexMetrica
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator
import org.koin.android.architecture.ext.getViewModel

/**
 * Created by isanechek on 9/26/17.
 */
class CategoryFragment : BaseFragment(), CategoryAdapter.ItemClickListener {

    // view's
    private lateinit var progressErrorContainer: View // default state gone
    private lateinit var progressView: BernoullisProgressView // default state visible
    private lateinit var errorContainer: View // default state gone
    private lateinit var errorInfoTv: AppCompatTextView // default state visible
    private lateinit var errorRetryBtn: AppCompatButton // default state visible
    private lateinit var list: RecyclerView // default state gone

    private var _adapter: CategoryAdapter? = null
    private lateinit var viewModel: CategoryViewModel

    override fun layoutResId(): Int = _layout.category_screen_layout

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressErrorContainer = view.findViewById(_id.progress_view_container)
        progressView = view.findViewById(_id.progress_view_progress)
        errorContainer = view.findViewById(_id.progress_view_error_container)
        errorInfoTv = view.findViewById(_id.progress_view_error_tv)
        errorRetryBtn = view.findViewById(_id.progress_view_error_btn)
        list = view.findViewById(_id.category_screen_list)
        list.show()

        _adapter = CategoryAdapter()
        _adapter?.setOnClickListener(this)
        with(list) {
            layoutManager = GridLayoutManager(activity, 2, GridLayoutManager.HORIZONTAL, false)
            adapter = _adapter
            setHasFixedSize(true)
            setItemViewCacheSize(10)
            val animator = SlideInUpAnimator(OvershootInterpolator(1f))
            animator.addDuration = 350
            itemAnimator = animator
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = getViewModel()
        initObserver()
    }

    override fun onItemClickListener(view: View, position: Int, category: String) {
        YandexMetrica.reportEvent("open category", category)
        val bundle = TimelineFragment.getBundle(category)
        goTo<TimelineFragment>(
            keepState = false,
            withCustomAnimation = true,
            arg = bundle
        )
    }

    override fun getTitle(): String = NavigationId.CATEGORY.name

    private fun initObserver() {
        viewModel.status.observe(this, Observer { status ->
            if (status != null) {
                when {
                    status.status == Status.LOADING -> when {
                        status.message == Const.REQUEST_DATA_AND_SHOW_BIG_PROGRESS -> {
                            progressErrorContainer.show()
                            progressView.show()
                        }
                        status.message == STATUS_HIDE_BIG_AND_SHOW_SMALL_PROGRESS -> {
                            progressView.hide()
                            progressErrorContainer.hide()
//                            list.show()
                        }
                        status.message == STATUS_HIDE_PROGRESS -> {
                            progressView.hide()
                            progressErrorContainer.hide()
//                            list.show()
                        }
                    }
//                    status.status == Status.SUCCESS -> {
//                        progressView.hide()
//                        progressErrorContainer.hide()
//                        list.show()
//                    }
                    status.status == Status.ERROR -> {
                        progressView.hide()
                        errorContainer.show()
                        errorInfoTv.text = getString(R.string.load_data_with_error_text)
                        errorRetryBtn.onClick {
                            viewModel.load(RequestStrategy.DATA_REQUEST)
                        }
                    }
                }
            }
        })

        viewModel.loadCategory.observe(this, Observer { response ->
            _adapter?.submitList(response)
        })

        connection.observe(this, Observer { conn ->
            conn ?: return@Observer
            when(conn.type) {
                Connection.WIFI -> {
                    viewModel.load(RequestStrategy.DATA_REQUEST)
                }
                Connection.MOBILE -> {
                    viewModel.load(RequestStrategy.DATA_REQUEST)
                }
                Connection.OFFLINE -> {
                }
            }
        })
    }

}