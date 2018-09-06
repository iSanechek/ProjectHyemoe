package com.list.rados.fastlist

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer

/**
 * Created by Radoslav Yankov on 29.06.2018
 * radoslavyankov@gmail.com
 * Original https://github.com/dev-labs-bg/fast-list
 */

class FastListViewHolder<T>(override val containerView: View, val holderType: Int)
    : RecyclerView.ViewHolder(containerView), LayoutContainer {
    fun bind(entry: T, func: View.(item: T) -> Unit) {
        containerView.apply {
            func(entry)
        }
    }
}