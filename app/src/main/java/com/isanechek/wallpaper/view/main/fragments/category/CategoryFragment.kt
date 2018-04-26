package com.isanechek.wallpaper.view.main.fragments.category

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.animation.OvershootInterpolator
import com.isanechek.wallpaper.data.network.RequestStrategy
import com.isanechek.wallpaper.utils._id
import com.isanechek.wallpaper.utils._layout
import com.isanechek.wallpaper.view.base.BaseFragment
import com.isanechek.wallpaper.view.main.fragments.timeline.TimelineFragment
import com.isanechek.wallpaper.view.widgets.navigation.NavigationId
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator
import org.koin.android.architecture.ext.getViewModel

/**
 * Created by isanechek on 9/26/17.
 */
class CategoryFragment : BaseFragment(), CategoryAdapter.ItemClickListener {

    private var _adapter: CategoryAdapter? = null
    private lateinit var viewModel: CategoryViewModel

    // view's
    private lateinit var list: RecyclerView

    override fun layoutResId(): Int = _layout.category_fragment_layout

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        list = view.findViewById(_id.category_screen_list)

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
        viewModel.loadCategory.observe(this, Observer { response ->
            if (response != null) {
                _adapter?.submitList(response)
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