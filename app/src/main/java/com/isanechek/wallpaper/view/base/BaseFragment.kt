package com.isanechek.wallpaper.view.base

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.isanechek.wallpaper.utils.extensions.emptyString
import com.isanechek.wallpaper.utils.network.ConnectionLiveData
import com.isanechek.wallpaper.view.navigation.BackStrategy
import com.isanechek.wallpaper.view.navigation.Navigator
import org.koin.android.ext.android.inject

/**
 * Created by isanechek on 11/17/17.
 */
abstract class BaseFragment : Fragment() {

    val connection by inject<ConnectionLiveData>()
    lateinit var navigator: Navigator
    protected lateinit var activity: BaseActivity

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is BaseActivity) {
            this.activity = context
            navigator = activity.getInstanceNavigator()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(layoutResId(), container, false)

    protected abstract fun layoutResId(): Int

    inline fun <reified T : Fragment> goTo(
        keepState: Boolean = true,
        withCustomAnimation: Boolean = false,
        arg: Bundle = Bundle.EMPTY,
        shared: Pair<String, View>? = null,
        backStrategy: BackStrategy = BackStrategy.KEEP
    ) {
        navigator.goTo<T>(
            keepState = keepState,
            withCustomAnimation = withCustomAnimation,
            arg = arg,
            shared = shared,
            backStrategy = backStrategy
        )
    }

    open fun getTitle(): String = emptyString
}