package com.isanechek.wallpaper.view.main

import android.arch.lifecycle.Observer
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.widget.CardView
import android.support.v7.widget.Toolbar
import android.view.View
import com.isanechek.wallpaper.utils.*
import com.isanechek.wallpaper.utils.extensions.*
import com.isanechek.wallpaper.utils.pref.Preferences
import com.isanechek.wallpaper.view.base.BaseActivity
import com.isanechek.wallpaper.view.base.BaseFragment
import com.isanechek.wallpaper.view.main.fragments.category.CategoryFragment
import com.isanechek.wallpaper.view.main.fragments.timeline.TimelineFragment
import com.isanechek.wallpaper.view.widgets.AnimatedImageView
import com.isanechek.wallpaper.view.widgets.AnimatedTextView
import com.isanechek.wallpaper.view.widgets.ArcView
import com.isanechek.wallpaper.view.widgets.navigation.NavAdapterItemSelectedListener
import com.isanechek.wallpaper.view.widgets.navigation.NavigationDrawerView
import com.isanechek.wallpaper.view.widgets.navigation.NavigationItem
import org.koin.android.architecture.ext.getViewModel
import com.isanechek.wallpaper.view.widgets.navigation.NavigationId as Id


class MainActivity : BaseActivity(), NavAdapterItemSelectedListener {

    private val TRANSLATION_X_KEY = "TRANSLATION_X_KEY"
    private val CARD_ELEVATION_KEY = "CARD_ELEVATION_KEY"
    private val SCALE_KEY = "SCALE_KEY"

    // view's
    private val toolbar: Toolbar by lazy { findViewById<Toolbar>(_id.toolbar) }
    private val toolbarTitle: AnimatedTextView by lazy { findViewById<AnimatedTextView>(_id.toolbarTitle) }
    private val arcImage: AnimatedImageView by lazy { findViewById<AnimatedImageView>(_id.arcImage) }
    private val arcView: ArcView by lazy { findViewById<ArcView>(_id.arcView) }
    private val navView: NavigationDrawerView by lazy { findViewById<NavigationDrawerView>(_id.navView) }
    private val drawer: DrawerLayout by lazy { findViewById<DrawerLayout>(_id.drawerLayout) }
    private val mainView: CardView by lazy { findViewById<CardView>(_id.mainView) }

    private lateinit var viewModel: MainViewModel
    private val pref: Preferences by lazy { Preferences() }
    private var category = pref.defaultCategory

    private var isArcIcon = false
    private var isDrawerOpened = false
    private var currentNavigationSelectedItem = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(_layout.main_activity_layout)
        viewModel = getViewModel()
        initViews()
        setupObservers()
        logger("Hello from main activity")
        logger("Pref category $category")
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

    override fun onDestroy() {
        viewModel.saveNavigationState(navigator.getState())
        super.onDestroy()
    }

    override fun onBackPressed() {
        when {
            drawer.isDrawerOpen(GravityCompat.START) -> drawer.closeDrawer(GravityCompat.START)
            navigator.hasBackStack() -> navigator.goBack()
            else -> super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: NavigationItem) {
        logger("NavigationItemSelected ${item.id.name}")
        when (item.id) {
            Id.CATEGORY -> goTo<CategoryFragment>()
            Id.TIMELINE -> goTo<TimelineFragment>()
        }
        drawer.closeDrawer(GravityCompat.START)
    }

    override fun onFragmentChanged(currentTag: String, currentFragment: Fragment) {
        val tag = (currentFragment as? BaseFragment)?.getTitle() ?: emptyString

        toolbarTitle.setAnimatedText(tag, 100)
        if (currentTag == Id.DETAIL.fullName) {
            isArcIcon = true
            setArcArrowState()
            toolbar.setBackgroundColor(Color.TRANSPARENT)
        } else if (isArcIcon) {
            isArcIcon = false
            setArcHamburgerIconState()
            toolbar.setBackgroundColor(takeColor(_color.bg_color))
        }

        val checkPosition = when(tag) {
            Id.CATEGORY.fullName -> 0
            Id.TIMELINE.fullName -> 1
            else -> currentNavigationSelectedItem
        }

        if (currentNavigationSelectedItem != checkPosition) {
            currentNavigationSelectedItem = checkPosition
            navView.setChecked(currentNavigationSelectedItem)
        }
    }

    private fun initViews() {
        // toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        if (isArcIcon || isDrawerOpened) setArcArrowState()
        else setArcHamburgerIconState()

        // navView
        navView.navigationItemSelectListener = this
//        navView.header.

        // drawer layout
        drawer.drawerElevation = 0F
        drawer.addDrawerListener(object: DrawerLayout.SimpleDrawerListener() {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                super.onDrawerSlide(drawerView, slideOffset)
                val moveFactor = navView.width * slideOffset
                mainView.translationX = moveFactor
                mainView.scale = 1 - slideOffset / 4
                mainView.cardElevation = slideOffset * 10.toPx(this@MainActivity)
            }

            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)
                if (!isArcIcon) {
                    setArcArrowState()
                    isDrawerOpened = true
                }
            }

            override fun onDrawerClosed(drawerView: View) {
                super.onDrawerClosed(drawerView)
                if (!isArcIcon && isDrawerOpened) {
                    setArcHamburgerIconState()
                    isDrawerOpened = false
                }
            }
        })
        drawer.setScrimColor(Color.TRANSPARENT)
    }

    private fun setArcArrowState() {
        arcView.onClick {
            super.onBackPressed()
        }
        arcImage.setAnimatedImage(_drawable.hamb)
    }

    private fun setArcHamburgerIconState() {
        arcView.onClick {
            drawer.openDrawer(GravityCompat.START)
        }
        arcImage.setAnimatedImage(_drawable.hamb)
    }

    private fun setupObservers() {
        viewModel.getNavigationState().observe(this, Observer { state ->
            if (state == null) {
                logger("State null")
                if (category == Const.EMPTY) {
                    logger("Show Category Fragment")
                    goTo<CategoryFragment>()
                } else {
                    logger("Show Timeline Fragment with category $category")
                    val bundle = TimelineFragment.getBundle(category)
                    goTo<TimelineFragment>(
                            keepState = true,
                            withCustomAnimation = false,
                            arg = bundle)
                }
            } else {
                logger("Restore state")
                navigator.restore(state)
            }
        })
    }
}
