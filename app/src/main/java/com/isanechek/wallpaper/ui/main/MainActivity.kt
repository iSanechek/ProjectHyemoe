package com.isanechek.wallpaper.ui.main

import _drawable
import _id
import _layout
import android.app.WallpaperManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.hugocastelani.waterfalltoolbar.Dp
import com.isanechek.common.DebugUtils
import com.isanechek.extensions.*
import com.isanechek.repository.Response
import com.isanechek.repository.Status.*
import com.isanechek.wallpaper.R
import com.isanechek.wallpaper.common.PrefManager
import com.isanechek.wallpaper.models.Update
import com.isanechek.wallpaper.ui.about.AboutFragment
import com.isanechek.wallpaper.ui.base.BaseActivity
import com.isanechek.wallpaper.ui.base.BaseFragment
import com.isanechek.wallpaper.ui.category.CategoryFragment
import com.isanechek.wallpaper.ui.dialogs.NoWallpaperBottomDialog
import com.isanechek.wallpaper.ui.splash.ARGS_KEY
import com.isanechek.wallpaper.ui.splash.ARGS_PATH
import com.isanechek.wallpaper.ui.widgets.AnimatedImageView
import com.isanechek.wallpaper.ui.widgets.AnimatedTextView
import com.isanechek.wallpaper.ui.widgets.navigation.NavAdapterItemSelectedListener
import com.isanechek.wallpaper.ui.widgets.navigation.NavigationDrawerView
import com.isanechek.wallpaper.ui.widgets.navigation.NavigationItem
import com.isanechek.wallpaper.utils.network.Connection
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation
import kotlinx.android.synthetic.main.main_screen_content_layout.*
import org.koin.android.ext.android.inject
import java.io.IOException
import com.isanechek.wallpaper.ui.widgets.navigation.NavigationId as Id


private const val TRANSLATION_X_KEY = "TRANSLATION_X_KEY"
private const val CARD_ELEVATION_KEY = "CARD_ELEVATION_KEY"
private const val SCALE_KEY = "SCALE_KEY"

class MainActivity : BaseActivity(), NavAdapterItemSelectedListener {

    // view's
    private val toolbar: Toolbar by lazy {
        findViewById<Toolbar>(_id.main_screen_toolbar)
    }
    private val toolbarTitle: AnimatedTextView by lazy {
        findViewById<AnimatedTextView>(_id.main_screen_title_toolbar_tv)
    }

    private val navView: NavigationDrawerView by lazy {
        findViewById<NavigationDrawerView>(_id.navView)
    }
    private val drawer: androidx.drawerlayout.widget.DrawerLayout by lazy {
        findViewById<androidx.drawerlayout.widget.DrawerLayout>(_id.drawerLayout)
    }
    private val mainView: CardView by lazy {
        findViewById<CardView>(_id.mainView)
    }

    // navigation
    private val navBtn: AnimatedImageView by lazy {
        findViewById<AnimatedImageView>(_id.main_screen_navigation_btn)
    }
    // ads
    private val adsInfoBtn: AnimatedImageView by lazy {
        findViewById<AnimatedImageView>(_id.main_screen_ads_info_btn)
    }

    // header
    private val androidIcon: ImageView by lazy {
        findViewById<ImageView>(_id.header_iv)
    }
    private val lastUpdateData: TextView by lazy {
        findViewById<TextView>(_id.header_update_data_tv)
    }

    private val viewModel by inject<MainViewModel>()
    private val pref by inject<PrefManager>()
    private val debug by inject<DebugUtils>()

    private var isDrawerOpened = false
    private var categoryScreen = false
    private var currentNavigationSelectedItem = 0


    private val menuClickListener = Toolbar.OnMenuItemClickListener { p0 ->
        p0?.let {
            when(it.itemId) {
                _id.remove_wallpaper -> showNoWallpaperDialog()
            }
        }
        false
    }

    private val key: String
        get() = intent.getStringExtra(ARGS_KEY)

    private val path: String
        get() = intent.getStringExtra(ARGS_PATH)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(_layout.main_screen_layout)
        initViews()
        initObserver()
        goTo<CategoryFragment>(arg = Bundle().apply {
            putString("key.category", key)
            putString("path.category", path)
        })

        viewModel.getNavState()
                ?.let {
                    navigator.restore(it)
                }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        fun put(key: String, value: Float) = outState?.putFloat(key, value)
        with(mainView) {
            put(TRANSLATION_X_KEY, translationX)
            put(CARD_ELEVATION_KEY, scale)
            put(SCALE_KEY, cardElevation)

        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        savedInstanceState?.let {
            with(mainView) {
                translationX = it.getFloat(TRANSLATION_X_KEY)
                scale = it.getFloat(CARD_ELEVATION_KEY)
                cardElevation = it.getFloat(SCALE_KEY)
            }
        }
    }

    override fun onBackPressed() {
        when {
            drawer.isDrawerOpen(GravityCompat.START) -> drawer.closeDrawer(GravityCompat.START)
            navigator.hasBackStack() -> navigator.goBack()
            else -> super.onBackPressed()
        }
    }

    override fun onDestroy() {
        viewModel.saveNavState(navigator.getState())
        super.onDestroy()
    }

    override fun onNavigationItemSelected(item: NavigationItem) {
        when (item.id) {
            Id.CATEGORY -> goTo<CategoryFragment>()
            Id.ABOUT -> goTo<AboutFragment>()
            Id.NOWALLPAPER -> showNoWallpaperDialog()
        }
        drawer.closeDrawer(GravityCompat.START)
    }

    override fun onFragmentChanged(currentTag: String, currentFragment: androidx.fragment.app.Fragment) {
        val tag = (currentFragment as? BaseFragment)?.getTitle() ?: emptyString

        when (currentTag) {
            Id.ABOUT.fullName -> {
                categoryScreen = false
                setTitle(tag)
                setArcArrowState(true)
            }
            Id.CATEGORY.fullName -> {
                categoryScreen = true
                if (waterfallToolbar.elevation != 0f)
                    waterfallToolbar.elevation = 0f
                setTitle(tag)
                setArcHamburgerIconState()
            }
            Id.TIMELINE.fullName -> {
                categoryScreen = false

                setTitle(tag)
                setArcArrowState(true)
            }
        }

        val checkPosition = when (tag) {
            Id.CATEGORY.fullName -> 0
            Id.TIMELINE.fullName -> 1
            else -> currentNavigationSelectedItem
        }

        if (currentNavigationSelectedItem != checkPosition) {
            currentNavigationSelectedItem = checkPosition
            navView.setChecked(currentNavigationSelectedItem)
        }
    }

    override fun setupWaterfall(recycler: RecyclerView) {
        waterfallToolbar.recyclerView = recycler
    }

    private fun setTitle(title: String) {
        toolbarTitle.setAnimatedText(title, 250)
    }

    private fun initViews() {
        // toolbar
        toolbar.inflateMenu(R.menu.toolbar_manu)
        toolbar.setOnMenuItemClickListener(menuClickListener)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        if (isDrawerOpened)
            if (categoryScreen) setArcArrowState()
            else setArcArrowState(true)
        else setArcHamburgerIconState()

        adsInfoBtn.visibility = View.GONE
        adsInfoBtn.setAnimatedImage(
                _drawable.ic_error_outline_black_24dp,
                100)

        // navView
        navView.navigationItemSelectListener = this

        with(drawer) {
            drawerElevation = 0F
            addDrawerListener(object : DrawerLayout.SimpleDrawerListener() {
                override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                    super.onDrawerSlide(drawerView, slideOffset)
                    val moveFactor = navView.width * slideOffset
                    mainView.translationX = moveFactor
                    mainView.scale = 1 - slideOffset / 4
                    mainView.cardElevation = slideOffset * 10.toPx(this@MainActivity)
                }

                override fun onDrawerOpened(drawerView: View) {
                    super.onDrawerOpened(drawerView)
                    isDrawerOpened = true
                    if (categoryScreen) {
                        setArcArrowState()
                    } else setArcArrowState(true)

                    lastUpdateData.text = pref.lastUpdateData.toStringData()
                    Picasso.get()
                            .load(R.mipmap.ic_launcher)
                            .transform(RoundedCornersTransformation(8, 0))
                            .into(androidIcon)
                }

                override fun onDrawerClosed(drawerView: View) {
                    super.onDrawerClosed(drawerView)
                    if (isDrawerOpened) {
                        setArcHamburgerIconState()
                        isDrawerOpened = false
                    }
                }
            })
            setScrimColor(Color.TRANSPARENT)
        }
    }

    private fun setArcArrowState(arrow: Boolean = false) {
        if (arrow) {
            navBtn.setAnimatedImage(_drawable.navigation_back_24dp, 100)
        } else navBtn.setAnimatedImage(_drawable.navigation_open_24dp, 100)

        navBtn.onClick {
            super.onBackPressed()
        }
    }

    private fun setArcHamburgerIconState() {
        navBtn.setAnimatedImage(_drawable.navigation_closed_24dp, 100)

        navBtn.onClick {
            drawer.openDrawer(GravityCompat.START)
        }
    }

    private lateinit var snackbar: Snackbar
    private fun initObserver() {
        connection.observe(this, Observer { conn ->
            conn ?: return@Observer
            when(conn.type) {
                Connection.WIFI -> {}
                Connection.MOBILE -> {}
                Connection.OFFLINE -> {
                    snackbar = Snackbar
                            .make(
                                    findViewById(android.R.id.content),
                                    "No Connection",
                                    Snackbar.LENGTH_INDEFINITE)
                            .setAction("Ok") { snackbar.dismiss() }
                }
            }
        })

        viewModel.updateStatus.observe(this, Observer<Response<Update>> { status ->
            status ?: return@Observer

            when(status.status) {
                RUNNING -> {}
                FAILED -> {}
                SUCCESS -> {
                    status.data?.let {
                        val dialog = AlertDialog.Builder(this@MainActivity)
                                .setTitle("New version!")
                                .setMessage("Update?")
                                .setPositiveButton("update") { dialog, _ ->
                                    dialog?.dismiss()
                                    viewModel.updateNow(this@MainActivity, it.url)
                                }
                                .setNeutralButton("later") { dialog, _ ->
                                    dialog?.dismiss()
                                    viewModel.updateLater()
                                }
                                .setNegativeButton("forget") { dialog, _ ->
                                    dialog?.dismiss()
                                    viewModel.forgetThisUpdate()
                                }
                        dialog.show()
                    }
                }
                INITIAL -> TODO()
                BAD_REQUEST -> TODO()
                NOT_FIND -> TODO()
            }
        })
    }

    private fun showNoWallpaperDialog() {
        val dialog = NoWallpaperBottomDialog {
            try {
                val wm = WallpaperManager.getInstance(this@MainActivity)
                val bitmap = BitmapFactory.decodeResource(resources, _drawable.black)
                wm.setBitmap(bitmap)
            } catch (e: IOException) {
                debug.sendStackTrace(e, "Remove wallpaper error!")
            }
        }
        dialog.show(supportFragmentManager, "RemoveWallpaperDialog")


    }
}
