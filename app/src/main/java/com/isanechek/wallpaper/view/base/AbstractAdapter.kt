package com.isanechek.wallpaper.view.base

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.isanechek.wallpaper.utils.extensions.inflate

abstract class AbstractAdapter<ITEM> constructor(protected var itemList: List<ITEM>,
                                                 private val layoutResId: Int)
    : RecyclerView.Adapter<AbstractAdapter.Holder>() {

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = parent inflate layoutResId
        val viewHolder = Holder(view)
        val itemView = viewHolder.itemView
        itemView.setOnClickListener {
            val adapterPosition = viewHolder.adapterPosition
            if (adapterPosition != RecyclerView.NO_POSITION) {
                onItemClick(itemView, adapterPosition)
            }
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: AbstractAdapter.Holder, position: Int) {
        val item = itemList[position]
        holder.itemView.bind(item)
    }

    fun update(itemList: List<ITEM>) {
        this.itemList = itemList
        notifyDataSetChanged()//TODO
    }

    final override fun onViewRecycled(holder: AbstractAdapter.Holder) {
        super.onViewRecycled(holder)
        onViewRecycled(holder.itemView)
    }

    protected open fun onViewRecycled(itemView: View) {
    }

    protected open fun onItemClick(itemView: View, position: Int) {
    }

    open fun View.bind(item: ITEM){
        //NO-OP
    }

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView)
}