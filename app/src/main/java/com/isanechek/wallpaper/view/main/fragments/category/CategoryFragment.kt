package com.isanechek.wallpaper.view.main.fragments.category

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.view.View
import com.isanechek.wallpaper.data.network.RequestStrategy
import com.isanechek.wallpaper.utils._layout
import com.isanechek.wallpaper.utils.glide.TransformationType
import com.isanechek.wallpaper.utils.glide.load
import com.isanechek.wallpaper.utils.logger
import com.isanechek.wallpaper.view.base.BaseFragment
import com.isanechek.wallpaper.view.main.fragments.timeline.TimelineFragment
import com.isanechek.wallpaper.view.widgets.navigation.NavigationId
import kotlinx.android.synthetic.main.category_fragment_layout.*
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

    override fun layoutResId(): Int = _layout.category_fragment_layout

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = CategoryAdapter()
        adapter?.setOnClickListener(this)
        cat_list.layoutManager = GridLayoutManager(activity, 2, GridLayoutManager.HORIZONTAL, false)
        cat_list.adapter = adapter
        cat_list.setHasFixedSize(true)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = getViewModel()
        viewModel.loadCategory.observe(this, Observer { response ->
            if (response != null) {
                adapter?.submitList(response)
                if (response.size > 0 ) {
                    cat_cover_iv.load(response.get(0)?.cover, TransformationType.FILTER)
                }
            }
        })
        viewModel.load(RequestStrategy.DATA_REQUEST)
    }

    override fun onItemClickListener(view: View, position: Int, category: String) {
        val bundle = TimelineFragment.getBundle(category)
        goTo<TimelineFragment>(
                keepState = false,
                withCustomAnimation = false,
                arg = bundle)
    }

    override fun getTitle(): String = NavigationId.CATEGORY.name
}