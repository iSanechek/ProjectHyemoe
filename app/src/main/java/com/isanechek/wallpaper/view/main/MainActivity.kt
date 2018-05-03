package com.isanechek.wallpaper.view.main

import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.widget.CardView
import android.support.v7.widget.Toolbar
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import com.github.florent37.kotlin.pleaseanimate.please
import com.isanechek.wallpaper.utils.*
import com.isanechek.wallpaper.utils.extensions.emptyString
import com.isanechek.wallpaper.utils.extensions.onClick
import com.isanechek.wallpaper.utils.extensions.scale
import com.isanechek.wallpaper.utils.extensions.toPx
import com.isanechek.wallpaper.view.about.AboutFragment
import com.isanechek.wallpaper.view.base.BaseActivity
import com.isanechek.wallpaper.view.base.BaseFragment
import com.isanechek.wallpaper.view.main.fragments.category.CategoryFragment
import com.isanechek.wallpaper.view.widgets.AnimatedImageView
import com.isanechek.wallpaper.view.widgets.AnimatedTextView
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
    private val toolbar: Toolbar by lazy { findViewById<Toolbar>(_id.main_screen_toolbar) }
    private val toolbarTitle: AnimatedTextView by lazy { findViewById<AnimatedTextView>(_id.main_screen_title_toolbar_tv) }

    private val toolbarHelperView: View by lazy { findViewById<View>(_id.main_screen_toolbar_helper_view) }
    private val toolbarCustomContainer: FrameLayout by lazy { findViewById<FrameLayout>(_id.main_screen_custom_title_container) }
    private val toolbarCustomTitle: TextView by lazy { findViewById<TextView>(_id.main_screen_title_tv) }

    private val navView: NavigationDrawerView by lazy { findViewById<NavigationDrawerView>(_id.navView) }
    private val drawer: DrawerLayout by lazy { findViewById<DrawerLayout>(_id.drawerLayout) }
    private val mainView: CardView by lazy { findViewById<CardView>(_id.mainView) }

    private val navBtn: AnimatedImageView by lazy { findViewById<AnimatedImageView>(_id.main_screen_navigation_btn) }
    private val adsInfoBtn: AnimatedImageView by lazy { findViewById<AnimatedImageView>(_id.main_screen_ads_info_btn) }

    private lateinit var viewModel: MainViewModel

    private var isDrawerOpened = false
    private var categoryScreen = false
    private var currentNavigationSelectedItem = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(_layout.main_screen_layout)
        viewModel = getViewModel()
        initViews()
        goTo<CategoryFragment>()
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

    override fun onNavigationItemSelected(item: NavigationItem) {
        when (item.id) {
            Id.CATEGORY -> goTo<CategoryFragment>()
            Id.ABOUT -> goTo<AboutFragment>()
        }
        drawer.closeDrawer(GravityCompat.START)
    }

    override fun onFragmentChanged(currentTag: String, currentFragment: Fragment) {
        val tag = (currentFragment as? BaseFragment)?.getTitle() ?: emptyString

        if (currentTag == Id.ABOUT.fullName) {
            categoryScreen = false
            hideCustomToolbar(tag)
            setArcArrowState(true)
        } else if (currentTag == Id.CATEGORY.fullName) {
            categoryScreen = true

//            toolbar.setBackgroundColor(Color.TRANSPARENT)

            please(duration = 10) {
                animate(toolbarTitle) toBe {
                    invisible()
                }
//                animate(toolbar) toBe {
//                    backgroundAlpha(0f)
//                }
                animate(toolbarHelperView) toBe {
                    alpha(1f)
                    topOfItsParent()
                }
            }.thenCouldYou(duration = 10) {
                animate(toolbarCustomContainer) toBe {
                    alpha(1f)
                    topOfItsParent(52f)
                }
            }.thenCouldYou(duration = 10) {
                animate(toolbarCustomTitle) toBe {
                    toolbarCustomTitle.text = getString(_string.category_title)
                    visible()
                }
            }.start()

            setArcHamburgerIconState()
        } else if (currentTag == Id.TIMELINE.fullName) {
            categoryScreen = false
            hideCustomToolbar(tag)

            setArcArrowState(true)
        } else if (currentTag == Id.DETAILS.fullName) {
            toolbarTitle.setAnimatedText(tag, 75)
            toolbar.setBackgroundColor(Color.TRANSPARENT)
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

    private fun hideCustomToolbar(tag: String) {
        please(duration = 10) {
            animate(toolbar) toBe {
                backgroundAlpha(1f)
            }
        }.thenCouldYou(duration = 10) {
            animate(toolbarCustomTitle) toBe {
                invisible()
            }
        }.thenCouldYou(duration = 10) {
            animate(toolbarCustomContainer) toBe {
                outOfScreen(Gravity.TOP)
                alpha(0f)
            }
        }.thenCouldYou(duration = 10) {
            animate(toolbarHelperView) toBe {
                outOfScreen(Gravity.TOP)
                alpha(0f)
            }
//                toolbar.setBackgroundColor(
////                    ContextCompat.getColor(
////                        this@MainActivity,
////                        _color.my_primary_dark_color
////                    )
//                )
            animate(toolbarTitle) toBe {
                visible()
                toolbarTitle.setAnimatedText(tag, 75)
            }
        }.start()
    }

    private fun initViews() {
        // toolbar
        toolbar.setBackgroundColor(Color.TRANSPARENT)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        if (isDrawerOpened)
            if (categoryScreen) setArcArrowState()
            else setArcArrowState(true)
        else setArcHamburgerIconState()

        adsInfoBtn.setAnimatedImage(_drawable.ic_error_outline_black_24dp, 100)

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
}
