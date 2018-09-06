package com.list.rados.fastlist

import android.view.View
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by Radoslav Yankov on 29.06.2018
 * radoslavyankov@gmail.com
 * Original https://github.com/dev-labs-bg/fast-list
 */

/**
 * Dynamic list bind function. It should be followed by one or multiple .map calls.
 * @param items - Generic list of the items to be displayed in the list
 */
fun <T> RecyclerView.bind(items: List<T>): FastListAdapter<T> {
    layoutManager = LinearLayoutManager(context)
    return FastListAdapter(items.toMutableList(), this)
}

/**
 * Simple list bind function.
 * @param items - Generic list of the items to be displayed in the list
 * @param singleLayout - The layout that will be used in the list
 * @param singleBind - The "binding" function between the item and the layout. This is the standard "bind" function in traditional ViewHolder classes. It uses Kotlin Extensions
 * so you can just use the XML names of the views inside your layout to address them.
 */
fun <T> RecyclerView.bind(items: List<T>, @LayoutRes singleLayout: Int = 0, singleBind: (View.(item: T) -> Unit)): FastListAdapter<T> {
    layoutManager = LinearLayoutManager(context)
    return FastListAdapter(items.toMutableList(), this
    ).map(singleLayout, { true }, singleBind)
}


/**
 * Updates the list using DiffUtils.
 * @param newItems the new list which is to replace the old one.
 *
 * NOTICE: The comparator currently checks if items are literally the same. You can change that if you want,
 * by changing the lambda in the function
 */
fun <T> RecyclerView.update(newItems: List<T>) {
    (adapter as? FastListAdapter<T>)?.update(newItems) { o, n -> o == n }
}

/**
 * For Paged Adapter
 */

fun <T> RecyclerView.bind(diffCallback: DiffUtil.ItemCallback<T>): FastListPagedAdapter<T> {
    layoutManager = LinearLayoutManager(context)
    return FastListPagedAdapter(diffCallback, this)
}

fun <T> RecyclerView.bind(diffCallback: DiffUtil.ItemCallback<T>, @LayoutRes singleLayout: Int = 0, singleBind: (View.(item: T) -> Unit)): FastListPagedAdapter<T> {
    layoutManager = LinearLayoutManager(context)
    return FastListPagedAdapter(diffCallback, this
    ).map(singleLayout, { true }, singleBind)
}

fun <T> RecyclerView.bindGrid(diffCallback: DiffUtil.ItemCallback<T>, @LayoutRes singleLayout: Int = 0, singleBind: (View.(item: T) -> Unit)): FastListPagedAdapter<T> {
    layoutManager = GridLayoutManager(context, 2, GridLayoutManager.HORIZONTAL, false)
    return FastListPagedAdapter(diffCallback, this
    ).map(singleLayout, { true }, singleBind)
}