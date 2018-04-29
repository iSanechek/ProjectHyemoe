package com.isanechek.wallpaper.view.widgets.navigation

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.isanechek.wallpaper.utils._color
import com.isanechek.wallpaper.utils._id
import com.isanechek.wallpaper.utils._layout
import com.isanechek.wallpaper.utils.extensions.takeColor
import com.isanechek.wallpaper.utils.extensions.tint
import com.isanechek.wallpaper.view.base.AbstractAdapter

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
            itemText.setTextColor(context.takeColor(_color.my_accent_color))
        } else {
            itemText.setTextColor(context.takeColor(_color.blue_gray))
        }
    }
}