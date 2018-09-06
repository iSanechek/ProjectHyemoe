package com.list.rados.fastlist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by Radoslav Yankov on 29.06.2018
 * radoslavyankov@gmail.com
 * Original https://github.com/dev-labs-bg/fast-list
 */

open class FastListAdapter<T>(
        private var items: MutableList<T>,
        private var list: RecyclerView)
    : RecyclerView.Adapter<FastListViewHolder<T>>() {

    private var bindMap = mutableListOf<BindMap<T>>()
    private var typeCounter = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FastListViewHolder<T> {
        return bindMap.first { it.type == viewType }.let {
            FastListViewHolder(LayoutInflater.from(parent.context).inflate(it.layout,
                    parent, false), viewType)
        }
    }

    override fun onBindViewHolder(holder: FastListViewHolder<T>, position: Int) {
        val item = items.get(position)
        holder.bind(item, bindMap.first { it.type == holder.holderType }.bind)
    }

    override fun getItemCount() = items.size

    override fun getItemViewType(position: Int) = try {
        bindMap.first { it.predicate(items[position]) }.type
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
    fun map(@LayoutRes layout: Int, predicate: (item: T) -> Boolean, bind: View.(item: T) -> Unit): FastListAdapter<T> {
        bindMap.add(BindMap(layout, typeCounter++, bind, predicate))
        list.adapter = this
        return this
    }

    /**
     * Sets up a layout manager for the recycler view.
     */
    fun layoutManager(manager: RecyclerView.LayoutManager): FastListAdapter<T> {
        list.layoutManager = manager
        return this
    }

    fun update(newList: List<T>, compare: (T, T) -> Boolean) {
        val diff = DiffUtil.calculateDiff(object : DiffUtil.Callback() {

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return compare(items[oldItemPosition], newList[newItemPosition])
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return items[oldItemPosition] == newList[newItemPosition]
            }

            override fun getOldListSize() = items.size

            override fun getNewListSize() = newList.size
        })
        items = newList.toMutableList()
        diff.dispatchUpdatesTo(this)
    }

}