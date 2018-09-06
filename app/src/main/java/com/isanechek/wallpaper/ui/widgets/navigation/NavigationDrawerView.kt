package com.isanechek.wallpaper.ui.widgets.navigation

import android.content.Context
import android.graphics.Color
import android.os.Parcel
import android.os.Parcelable
import com.google.android.material.navigation.NavigationView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.isanechek.wallpaper.R
import com.isanechek.extensions.delay
import com.isanechek.extensions.nonSafeLazy

class NavigationDrawerView : NavigationView, NavItemClickListener {

    private var itemList = mutableListOf(
            NavigationItem(
                    item = NavigationId.CATEGORY,
                    icon = com.isanechek.wallpaper.R.drawable.ic_bubble_chart_black_24dp,
                    isSelected = true,
                    itemIconColor = com.isanechek.wallpaper.R.color.my_primary_color
            ),
            NavigationItem(
                    item = NavigationId.NOWALLPAPER,
                    icon = com.isanechek.wallpaper.R.drawable.ic_broken_image_black_24dp,
                    itemIconColor = com.isanechek.wallpaper.R.color.my_primary_color
            ),
            NavigationItem(
                    item = NavigationId.ABOUT,
                    icon = com.isanechek.wallpaper.R.drawable.about,
                    itemIconColor = com.isanechek.wallpaper.R.color.my_primary_color
            )
    )

    private var currentSelectedItem: Int = 0

    private val adapter by nonSafeLazy {
        NavigationViewAdapter(itemList, this)
    }

    private val recyclerView by nonSafeLazy {
        RecyclerView(context).apply {
            layoutManager = LinearLayoutManager(context)
        }
    }

    var navigationItemSelectListener: NavAdapterItemSelectedListener? = null
    val header: View = getHeaderView(0)

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    init {
        setBackgroundColor(Color.TRANSPARENT)
        val layoutParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams.topMargin = context.resources.getDimension(R.dimen.nav_header_height).toInt()
        recyclerView.layoutParams = layoutParams
        recyclerView.adapter = adapter

        recyclerView.overScrollMode = View.OVER_SCROLL_NEVER
        addView(recyclerView)
    }

    override fun onSaveInstanceState(): Parcelable {
        val superState = super.onSaveInstanceState()
        val state = State(superState)
        state.currentPosition = currentSelectedItem
        return state
    }

    override fun onRestoreInstanceState(savedState: Parcelable?) {
        if (savedState !is State) {
            super.onRestoreInstanceState(savedState)
            return
        }
        super.onRestoreInstanceState(savedState.superState)
        this setCurrentSelected savedState.currentPosition
    }

    override fun invoke(item: NavigationItem, position: Int) {
        this setCurrentSelected position
        navigationItemSelectListener?.onNavigationItemSelected(item)
    }

    private infix fun setCurrentSelected(position: Int) {
        itemList[currentSelectedItem].isSelected = false
        currentSelectedItem = position
        itemList[currentSelectedItem].isSelected = true
    }

    fun setChecked(position: Int) {
        setCurrentSelected(position)
        //FIXME
        delay(250) {
            adapter.notifyDataSetChanged()
        }
    }

    private class State : androidx.customview.view.AbsSavedState {
        var currentPosition: Int = 0

        private constructor(parcel: Parcel) : super(parcel) {
            currentPosition = parcel.readInt() //FIXME
        }

        constructor(parcelable: Parcelable) : super(parcelable)

        override fun writeToParcel(dest: Parcel?, flags: Int) {
            super.writeToParcel(dest, flags)
            dest?.writeInt(currentPosition)
        }

        companion object CREATOR : Parcelable.Creator<State> {
            override fun createFromParcel(parcel: Parcel): State {
                return State(parcel)
            }

            override fun newArray(size: Int): Array<State?> {
                return arrayOfNulls(size)
            }
        }
    }
}