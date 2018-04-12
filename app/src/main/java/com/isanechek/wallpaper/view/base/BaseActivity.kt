package com.isanechek.wallpaper.view.base

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import com.isanechek.wallpaper.view.navigation.Navigator

/**
 * Created by isanechek on 11/17/17.
 */
abstract class BaseActivity : AppCompatActivity(), Navigator.FragmentChangeListener {

    lateinit var navigator: Navigator

    override fun onCreate(savedInstanceState: Bundle?) {
        navigator = Navigator(this, supportFragmentManager)
        navigator.fragmentChangeListener = this
        super.onCreate(savedInstanceState)
    }

    override fun onBackPressed() {
        if (navigator.hasBackStack())
            navigator.goBack()
        else
            super.onBackPressed()
    }

    protected inline fun <reified T : Fragment> goTo(keepState: Boolean = true,
                                                     withCustomAnimation: Boolean = false,
                                                     arg: Bundle = Bundle.EMPTY) {
        navigator.goTo<T>(keepState = keepState,
                withCustomAnimation = withCustomAnimation,
                arg = arg,
                shared = null)
    }

    fun getInstanceNavigator() : Navigator = navigator
}