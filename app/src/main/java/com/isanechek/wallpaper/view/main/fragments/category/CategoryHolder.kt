package com.isanechek.wallpaper.view.main.fragments.category

import android.os.Build
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.isanechek.wallpaper.R
import com.isanechek.wallpaper.data.database.Category
import com.isanechek.wallpaper.utils._id
import com.isanechek.wallpaper.utils.glide.TransformationType
import com.isanechek.wallpaper.utils.glide.load

/**
 * Created by isanechek on 9/26/17.
 */

class CategoryHolder(parent: ViewGroup?) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent?.context).inflate(R.layout.category_list_item_layout, parent, false)) {
    private var model: Category? = null
    private val root = itemView.findViewById<CardView>(_id.cat_item_root)
    private val title = itemView.findViewById<TextView>(_id.cat_item_title_tv)
    private val cover = itemView.findViewById<ImageView>(_id.cat_item_cover_iv)

    fun bindTo(model: Category?, position: Int, listener: CategoryAdapter.ItemClickListener?) {
        this.model = model

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cover.transitionName = "shared$position"
        }
        model?.let {
            title.text = it.title
            cover.load(it.cover, TransformationType.ROUND_WITH_FILTER)
            root.setOnClickListener {
                listener?.onItemClickListener(cover, position, model.title)
            }
        }
    }

}