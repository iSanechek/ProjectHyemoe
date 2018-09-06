package com.list.rados.fastlist

import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.isanechek.extensions.inflate

open class FastListPagedAdapter<T>(diffCallback: DiffUtil.ItemCallback<T>,
                                   private var list: RecyclerView)
    : PagedListAdapter<T, FastListViewHolder<T>>(diffCallback) {

    private var bindMap = mutableListOf<BindMap<T>>()
    private var typeCounter = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FastListViewHolder<T> =
            FastListViewHolder(parent.inflate(bindMap.first { it.type == viewType }.layout), viewType)

    override fun onBindViewHolder(holder: FastListViewHolder<T>, position: Int) {
        val item = getItem(position)
        item ?: return
        holder.bind(item, bindMap.first { it.type == holder.holderType }.bind)
    }

    override fun getItemViewType(position: Int): Int = try {
        bindMap.first { it.predicate(getItem(position)!!) }.type
    } catch (e: Exception) {
        0
    }

    /**
     * The function used for mapping types to layouts
     * @param layout - The ID of the XML layout of the given type
     * @param predicate - Function used to sort the items. For example, a Type field inside your items class with different values for different types.
     * @param bind - The "binding" function between the item and the layout. This is the standard "bind" function in traditional ViewHolder classes. It uses Kotlin Extensions
     * so you can just use the XML names of the views inside your layout to address them.
     */
    fun map(@LayoutRes layout: Int, predicate: (item: T) -> Boolean, bind: View.(item: T) -> Unit): FastListPagedAdapter<T> {
        bindMap.add(BindMap(layout, typeCounter++, bind, predicate))
        list.adapter = this
        return this
    }

    /**
     * Sets up a layout manager for the recycler view.
     */
    fun layoutManager(manager: RecyclerView.LayoutManager): FastListPagedAdapter<T> {
        list.layoutManager = manager
        return this
    }

}