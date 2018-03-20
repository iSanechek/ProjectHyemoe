package com.isanechek.wallpaper.view.main.fragments.category

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.view.View
import com.isanechek.wallpaper.data.network.RequestStrategy
import com.isanechek.wallpaper.utils._layout
import com.isanechek.wallpaper.utils.logger
import com.isanechek.wallpaper.view.base.BaseFragment
import com.isanechek.wallpaper.view.main.fragments.timeline.TimelineFragment
import com.isanechek.wallpaper.view.widgets.navigation.NavigationId
import kotlinx.android.synthetic.main.fragment_list_layout.*
import org.koin.android.architecture.ext.getViewModel

/**
 * Created by isanechek on 9/26/17.
 */
class CategoryFragment : BaseFragment(), CategoryAdapter.ItemClickListener {

    private var adapter: CategoryAdapter? = null
    private lateinit var viewModel: CategoryViewModel

    companion object {
        private val TAG = "CategoryFragment"
    }

    override fun layoutResId(): Int = _layout.fragment_list_layout

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = CategoryAdapter()
        adapter?.setOnClickListener(this)
        list_rv.adapter = adapter
        list_rv.setHasFixedSize(true)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = getViewModel()
        refreshHideIfShow()
        list_refresh.setOnRefreshListener { viewModel.load(RequestStrategy.UPDATE_REQUEST) }
        viewModel.loadCategory.observe(this, Observer { response ->
            if (response != null) {
                logger("$TAG Response size ${response.size}")
                refreshHideIfShow()
                adapter?.submitList(response)
            } else logger("$TAG Response NULL")
        })
        viewModel.load(RequestStrategy.DATA_REQUEST)
    }

    override fun onItemClickListener(view: View, position: Int, category: String) {
        logger("$TAG Item click pos -> $position cat -> ${category}")
        val bundle = TimelineFragment.getBundle(category)
        goTo<TimelineFragment>(
                keepState = false,
                withCustomAnimation = true,
                arg = bundle
        )
    }

    override fun getTitle(): String = NavigationId.CATEGORY.name

    private fun refreshHideIfShow() {
        list_refresh.let { if (it.isRefreshing) it.isRefreshing = false }
    }

}