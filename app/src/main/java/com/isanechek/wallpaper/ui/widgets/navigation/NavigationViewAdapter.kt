package com.isanechek.wallpaper.ui.widgets.navigation

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.isanechek.extensions.takeColor
import com.isanechek.extensions.tint
import com.isanechek.wallpaper.ui.base.AbstractAdapter

class NavigationViewAdapter constructor(
    navigationItemList: MutableList<NavigationItem>,
    private var itemClickListener: NavItemClickListener?
) : AbstractAdapter<NavigationItem>(navigationItemList, _layout.navigation_view_item) {

    override fun onItemClick(itemView: View, position: Int) {
        itemClickListener?.let {
            it(itemList[position], position)
        }
    }

    override fun View.bind(item: NavigationItem) {
        val itemText: TextView = findViewById(_id.itemText)
        val itemIcon: ImageView = findViewById(_id.itemIcon)
        itemText.text = item.name
        itemIcon.setImageResource(item.icon)
        itemIcon.tint(item.itemIconColor)
        if (item.isSelected) {
            itemText.setTextColor(context.takeColor(_color.my_primary_dark_color))
        } else {
            itemText.setTextColor(context.takeColor(_color.blue_gray))
        }
    }
}