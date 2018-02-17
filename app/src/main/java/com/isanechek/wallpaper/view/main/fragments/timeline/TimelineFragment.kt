package com.isanechek.wallpaper.view.main.fragments.timeline

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.isanechek.wallpaper.R
import com.isanechek.wallpaper.data.Message
import com.isanechek.wallpaper.data.database.Wallpaper
import com.isanechek.wallpaper.data.network.RequestStrategy
import com.isanechek.wallpaper.utils.Const
import com.isanechek.wallpaper.utils._layout
import com.isanechek.wallpaper.utils.extensions.extraWithKey
import com.isanechek.wallpaper.utils.logger
import com.isanechek.wallpaper.utils.pref.Preferences
import com.isanechek.wallpaper.view.base.BaseFragment
import com.isanechek.wallpaper.view.main.MainViewModel
import com.isanechek.wallpaper.view.main.fragments.details.DetailsFragment
import com.isanechek.wallpaper.view.widgets.ParallaxImageView
import com.isanechek.wallpaper.view.widgets.navigation.NavigationId
import kotlinx.android.synthetic.main.fragment_list_layout.*
import org.koin.android.architecture.ext.getViewModel

/**
 * Created by isanechek on 9/26/17.
 */
class TimelineFragment : BaseFragment(), SwipeRefreshLayout.OnRefreshListener, TimelineAdapter.ItemClickListener {

    companion object {
        private val TAG = "TimelineFragment"
        const val SAVE_CATEGORY_KEY = "save.category.key"
        fun getBundle(category: String) = Bundle().apply { putString(SAVE_CATEGORY_KEY, category) }
    }

    private lateinit var viewModel: TimelineViewModel
    private var adapter: TimelineAdapter? = null
    private var showMsgToolbar = false
    private lateinit var category: String
    private val pref by lazy { Preferences() }

    override fun layoutResId(): Int = _layout.fragment_list_layout

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (arguments != null) {
            if (arguments!!.containsKey(SAVE_CATEGORY_KEY)) {
                category = this extraWithKey SAVE_CATEGORY_KEY
                pref.defaultCategory = category
            } else {
                defaultCategory()
            }
        } else {
            defaultCategory()
        }
    }

    private fun defaultCategory() {
        category = pref.defaultCategory
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecycler()
        setupSwipeRefresh()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
//        initStatus()
        viewModel = getViewModel()
        viewModel.load(category, RequestStrategy.DATA_REQUEST)
        setupRequestObserver(category)
    }

//    private fun initStatus() {
//        viewModel.status.observe(this, Observer { response ->
//            if (response != null) {
//                if (response.status == Status.LOADING) {
//                    // show progress
//
//                    // show message status
//                    val message = response.message?.let { HelperUtil.getStatusMessage(it, activity) }
//                    logger("$TAG status loading message $message")
//                    when {
//                        showMsgToolbar -> showMessage(message)
//                        else -> showMessage(message, true)
//                    }
//                } else if (response.status == Status.SUCCESS) {
//                    // hide progress
//                    hideProgress()
//                    // action with data
//                    if (response.message == Const.FIRST_START_OR_EMPTY_RESPONSE_CATEGORY) {
////                        category = response.data as String
//                        logger("$TAG status loading success data $category")
//                        setupRequestObserver(category)
//                    }
//                } else if (response.status == Status.ERROR) {
//                    // hide progress
//                    hideProgress()
//                    // show error message
//                    val errorMessage = response.message
//                    logger("$TAG status error message $errorMessage")
//                }
//            }
//        })
//    }

    private fun setupRequestObserver(args: String) {
        viewModel.loadWallpapers(args).observe(this, Observer { response ->
            if (response != null) {
                logger("$TAG Response ${response.size}")
                if (response.size > 0) {
                    hideProgress()
                    showMsgToolbar = true
                    adapter?.setList(response)
                }
            }
        })
    }

    private fun sendDataToActivity(key: String, message: String) {
        ViewModelProviders.of(activity).get(MainViewModel::class.java).sendMessage(Message(key, message))
    }

    private fun showMessage(msg: String?, update: Boolean = false) {
        if (msg == null) return
        if (showMsgToolbar) {
            // hide textview

            // send message to toolbar
            if (update) {
                logger("$TAG send message for toolbar title")
                sendDataToActivity(Const.MSG_FOR_TOOLBAR_TITLE, msg)
            } else {
                logger("$TAG send message for toolbar subtitle")
                sendDataToActivity(Const.MSG_FOR_TOOLBAR_SUBTITLE, msg)
            }
        } else {
            // show textview and message
            logger("$TAG show message in textview")
        }
    }

    private fun hideProgress() {
        if (list_refresh.isRefreshing) list_refresh.isRefreshing = false
    }

    private fun setupSwipeRefresh() {
        list_refresh.setOnRefreshListener(this)
        list_refresh.setColorSchemeColors(ContextCompat.getColor(activity, R.color.colorAccent_light))
    }

    private fun setupRecycler() {
        list_rv.layoutManager = LinearLayoutManager(activity)
        list_rv.tag = ParallaxImageView.RECYCLER_VIEW_TAG
        list_rv.setHasFixedSize(true)
        adapter = TimelineAdapter()
        adapter?.let { list_rv.adapter = it }
        adapter?.setOnClickItemListener(this)
    }

    override fun onRefresh() {
        logger("$TAG Refresh $category")
        viewModel.load(category, RequestStrategy.UPDATE_REQUEST)
    }

    override fun onItemClick(view: View, position: Int, id: String, preview: Wallpaper) {
        logger("$TAG Item click $position $id")
        val bundle = DetailsFragment.arg(preview)
        goTo<DetailsFragment>(
                keepState = false,
                withCustomAnimation = false,
                arg = bundle)
    }

    override fun getTitle(): String = if (category == Const.EMPTY) NavigationId.TIMELINE.name else category
}