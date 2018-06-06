package com.isanechek.wallpaper.view.about

import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.appcompat.R.attr.selectableItemBackground
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.isanechek.wallpaper.BuildConfig
import com.isanechek.wallpaper.R
import com.isanechek.wallpaper.utils.*
import com.isanechek.wallpaper.utils.extensions.*
import com.isanechek.wallpaper.view.base.BaseFragment

/**
 * Created by isanechek on 4/23/18.
 */
private const val EMAIL = 0
private const val TWITTER = 1
private const val FACEBOOK = 2
private const val VK = 3
private const val INSTAGRAM = 4
private const val WEB = 5
private const val GITHUB = 7
private const val INFO = 6
private const val YOUTUBE = 8
private const val GPLUS = 9

data class AboutItem(val id: Int, val title: String, val icon: Int, val data: Int)

class AboutFragment : BaseFragment(), View.OnClickListener {

    private lateinit var appIcon: ImageView

    override fun layoutResId(): Int = _layout.about_screen_layout

    override fun getTitle(): String = getString(R.string.about_screen_title)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        appIcon = view.findViewById(_id.app_icon)
        val con = view.findViewById<LinearLayout>(_id.content_container)
        listOf(
                AboutItem(id = EMAIL, title = getString(_string.email), icon =_drawable.email, data = _string.mail),
                AboutItem(id = WEB, title = getString(_string.web_site), icon =_drawable.site_24dp, data = _string.site_url),
                AboutItem(id = FACEBOOK, title = getString(_string.facebook), icon =_drawable.facebook, data = _string.facebook_url),
                AboutItem(id = TWITTER, title = getString(_string.twitter), icon =_drawable.twitter, data = _string.twitter_url),
                AboutItem(id = VK, title = getString(_string.vk_title), icon =_drawable.vk_icon, data = _string.vk_url),
                AboutItem(id = INSTAGRAM, title = getString(_string.instagram_title), icon =_drawable.instagram_icon, data = _string.instagram_url),
                AboutItem(id = YOUTUBE, title = getString(_string.youtube_title), icon =_drawable.youtube, data = _string.youtube_url),
                AboutItem(id = GPLUS, title = getString(_string.g_plus_title), icon =_drawable.gplus, data = _string.g_plus_url),
                AboutItem(id = INFO, title = """${getString(_string.app_name)} ${BuildConfig.VERSION_NAME}
        |AverdSoft (c)
    """.trimMargin(), icon =_drawable.about, data = _string.app_name))
                .forEach {
                    con.addView(createView(it))
                }
    }

    private fun createView(item: AboutItem): View {
        val container = LinearLayout(activity)
        val llp = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT)
        llp.setMargins(0, 8, 0, 8)
        container.orientation = LinearLayout.VERTICAL
        container.layoutParams = llp

        val tv = TextView(activity)
        val llt = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)
        tv.layoutParams = llt
        val ov = TypedValue()
        context?.theme?.resolveAttribute(
                android.R.attr.selectableItemBackground,
                ov,
                true)
        with (tv) {
            id = item.id
            setPadding(8, 8, 8, 8)
            setTextColor(Color.parseColor("#ffffff"))
            text = item.title
            leftIcon(item.icon)
            textSize = 20f
            setBackgroundResource(ov.resourceId)
            compoundDrawablePadding = 16
            isClickable = true
            isFocusable = true
            setOnClickListener(this@AboutFragment)
        }
        container.addView(tv)
        return container
    }

    override fun onClick(v: View) {
        when (v.id) {
            EMAIL -> activity.sendEmail(getString(_string.app_name), getString(_string.mail), getString(_string.sen_us_emial))
            WEB -> actionView { _string.site_url }
            TWITTER -> actionView { _string.twitter_url}
            FACEBOOK -> actionView { _string.facebook_url }
            VK -> actionView { _string.vk_url}
            INSTAGRAM -> actionView { _string.instagram_url }
            GPLUS -> actionView { _string.g_plus_url }
            YOUTUBE -> actionView { _string.youtube_url }
        }
    }


    private inline fun actionView(action: () -> Int) {
        activity.actionView { getString(action()) }
    }
}