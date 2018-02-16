package com.isanechek.wallpaper.view.main.fragments.category

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import com.isanechek.wallpaper.R
import com.isanechek.wallpaper.data.database.Category

/**
 * Created by isanechek on 9/26/17.
 */
class CategoryHolder(parent: ViewGroup?) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent?.context).inflate(R.layout.category_item_layout2, parent, false)) {
    private val rootView = itemView.findViewById<FrameLayout>(R.id.category_item_root)
    private val tv = itemView.findViewById<TextView>(R.id.category_item_text)
    private lateinit var model: Category

    fun bindTo(model: Category?, position: Int, listener: CategoryAdapter.ItemClickListener?) {
        this.model = model!!
        rootView.setOnClickListener { listener?.onItemClickListener(it, position,  model.title) }
        model.title.let { tv.text = it }
    }
}