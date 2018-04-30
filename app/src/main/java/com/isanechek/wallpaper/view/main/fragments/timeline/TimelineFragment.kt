package com.isanechek.wallpaper.view.main.fragments.timeline

import android.arch.lifecycle.Observer
import android.content.Context
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.AppCompatButton
import android.support.v7.widget.AppCompatTextView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.animation.OvershootInterpolator
import com.isanechek.wallpaper.R
import com.isanechek.wallpaper.data.database.Wallpaper
import com.isanechek.wallpaper.data.network.RequestStrategy
import com.isanechek.wallpaper.data.network.Status
import com.isanechek.wallpaper.utils.*
import com.isanechek.wallpaper.utils.extensions.extraWithKey
import com.isanechek.wallpaper.utils.extensions.hide
import com.isanechek.wallpaper.utils.extensions.show
import com.isanechek.wallpaper.view.base.BaseFragment
import com.isanechek.wallpaper.view.details.DetailsFragment
import com.isanechek.wallpaper.view.navigation.BackStrategy
import com.isanechek.wallpaper.view.widgets.navigation.NavigationId
import com.vlad1m1r.lemniscate.BernoullisProgressView
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator
import org.koin.android.architecture.ext.getViewModel

/**
 * Created by isanechek on 9/26/17.
 */
class TimelineFragment : BaseFragment(), SwipeRefreshLayout.OnRefreshListener, TimelineAdapter.ItemClickListener {

    companion object {
        private val TAG = "TimelineFragment"
        const val SAVE_CATEGORY_KEY = "save.category.key"
        fun getBundle(category: String) = Bundle().apply {
            putString(SAVE_CATEGORY_KEY, category)
        }
    }

    // view's
    private lateinit var progressErrorContainer: View // default state gone
    private lateinit var progressView: BernoullisProgressView // default state visible
    private lateinit var errorContainer: View // default state gone
    private lateinit var errorInfoTv: AppCompatTextView // default state visible
    private lateinit var errorRetryBtn: AppCompatButton // default state visible
    private lateinit var srl: SwipeRefreshLayout // default state gone
    private lateinit var tl: RecyclerView // default state visible
    private lateinit var viewModel: TimelineViewModel

    private var _adapter: TimelineAdapter? = null
    private var showMsgToolbar = false
    private var category = Const.EMPTY

    override fun layoutResId(): Int = _layout.timeline_screen_layout

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (arguments != null && arguments!!.containsKey(SAVE_CATEGORY_KEY)) {
            category = this extraWithKey SAVE_CATEGORY_KEY
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressErrorContainer = view.findViewById(_id.progress_view_container)
        progressView = view.findViewById(_id.progress_view_progress)
        errorContainer = view.findViewById(_id.progress_view_error_container)
        errorInfoTv = view.findViewById(_id.progress_view_error_tv)
        errorRetryBtn = view.findViewById(_id.progress_view_error_btn)
        srl = view.findViewById(_id.list_refresh)
        tl = view.findViewById(_id.list_rv)
        setupSwipeRefresh()
        setupRecycler()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = getViewModel()
        viewModel.load(category, RequestStrategy.DATA_REQUEST)
        setupRequestObserver(category)
    }

    override fun onRefresh() {
        viewModel.load(category, RequestStrategy.UPDATE_REQUEST)
    }

    override fun onItemClick(view: View, position: Int, id: String, wallpaper: Wallpaper) {
        val args = DetailsFragment.args(wallpaper)
        goTo<DetailsFragment>(
            keepState = false,
            withCustomAnimation = true,
            arg = args,
            backStrategy = BackStrategy.DESTROY
        )


//        val f = DetailsFragment.create(wallpaper)
//        f.sharedElementEnterTransition = SharedTransitionSet()
//        f.enterTransition = Fade()
//        exitTransition = Fade()
//        f.sharedElementReturnTransition = SharedTransitionSet()
//
//        activity.supportFragmentManager
//            .beginTransaction()
//            .addSharedElement(view, getString(_string.wall_image))
//            .replace(_id.main_screen_fragment_contaner, f)
//            .addToBackStack(null)
//            .commit()
    }

    override fun getTitle(): String = if (category == Const.EMPTY) NavigationId.TIMELINE.name else category

    private fun setupRequestObserver(args: String) {
        viewModel.status.observe(this, Observer { response ->
            if (response != null) {
                if (response.status == Status.LOADING) {
                    // show progress
                    if (response.message == Const.REQUEST_DATA_AND_SHOW_BIG_PROGRESS) {
                        progressErrorContainer.show()
                        progressView.show()
                    } else if (response.message == STATUS_HIDE_PROGRESS) {
                        hideProgress()
                        if (progressErrorContainer.visibility == View.VISIBLE) {
                            progressView.hide()
                            progressErrorContainer.hide()
                            srl.show()
                        } else srl.show()
                    } else if (response.message == Const.REQUEST_UPDATE_DATE) {
                        srl.isRefreshing = true
                    }

                } else if (response.status == Status.SUCCESS) {
                   //
                } else if (response.status == Status.ERROR) {
                    // show error message
                    if (response.message == Const.BAD_REQUEST) {
                        progressView.hide()
                        errorContainer.show()
                        errorRetryBtn.isEnabled = false
                        errorInfoTv.text = getString(R.string.load_wallpapers_bad_request_text)
                    }
                }
            }
        })

        viewModel.loadWallpapers(args).observe(this, Observer { response ->
            if (response != null && response.size > 0) {
                hideProgress()
                showMsgToolbar = true
                _adapter?.submitList(response)
            }
        })
    }

    private fun hideProgress() {
        if (srl.isRefreshing) srl.isRefreshing = false
    }

    private fun setupSwipeRefresh() {
        with(srl) {
            setOnRefreshListener(this@TimelineFragment)
            setColorSchemeColors(ContextCompat.getColor(activity, _color.my_primary_dark_color))
        }
    }

    private fun setupRecycler() {
        _adapter = TimelineAdapter()
        _adapter?.setOnClickItemListener(this)
        with(tl) {
            layoutManager = LinearLayoutManager(activity)
            setHasFixedSize(true)
            adapter = _adapter
            val animator = SlideInUpAnimator(OvershootInterpolator(1f))
            animator.addDuration = 350
            animator.moveDuration = 150
            itemAnimator = animator
        }
    }
}