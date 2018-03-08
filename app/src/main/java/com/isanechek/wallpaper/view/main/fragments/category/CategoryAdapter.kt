package com.isanechek.wallpaper.view.main.fragments.category

import android.arch.paging.PagedListAdapter
import android.support.v7.recyclerview.extensions.DiffCallback
import android.view.View
import android.view.ViewGroup
import com.isanechek.wallpaper.data.database.Category

/**
 * Created by isanechek on 9/26/17.
 */
class CategoryAdapter : PagedListAdapter<Category, CategoryHolder>(diffCallback) {
    private var listener: ItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryHolder = CategoryHolder(parent)

    override fun onBindViewHolder(holder: CategoryHolder, position: Int) {
        holder?.bindTo(getItem(position), position, listener)
    }

    fun setOnClickListener(listener: ItemClickListener) {
        this.listener = listener
    }

    companion object {
        private val diffCallback = object : DiffCallback<Category>() {
            override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean = oldItem.title == newItem.title
            override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean = oldItem == newItem

            override fun getChangePayload(oldItem: Category, newItem: Category): Any {
                return super.getChangePayload(oldItem, newItem)
            }
        }
    }

    interface ItemClickListener {
        fun onItemClickListener(view: View, position: Int, category: String)
    }
}