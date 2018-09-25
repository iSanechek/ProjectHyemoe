package com.isanechek.wallpaper.ui.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.isanechek.wallpaper.utils.network.ConnectionLiveData
import com.isanechek.wallpaper.ui.navigation.Navigator
import com.yandex.metrica.YandexMetrica
import org.koin.android.ext.android.inject

/**
 * Created by isanechek on 11/17/17.
 */
abstract class BaseActivity : AppCompatActivity(), Navigator.FragmentChangeListener {

    val connection by inject<ConnectionLiveData>()

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

    override fun onResume() {
        super.onResume()
        YandexMetrica.resumeSession(this)
    }

    override fun onPause() {
        YandexMetrica.pauseSession(this)
        super.onPause()
    }

    protected inline fun <reified T : androidx.fragment.app.Fragment> goTo(keepState: Boolean = true,
                                                                           withCustomAnimation: Boolean = false,
                                                                           arg: Bundle = Bundle.EMPTY) {
        navigator.goTo<T>(keepState = keepState,
                withCustomAnimation = withCustomAnimation,
                arg = arg,
                shared = null)
    }

    fun getInstanceNavigator() : Navigator = navigator

    abstract fun setupWaterfall(recycler: RecyclerView)
}