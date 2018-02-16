package com.isanechek.wallpaper.view.base

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.isanechek.wallpaper.utils.extensions.emptyString
import com.isanechek.wallpaper.view.navigation.Navigator

/**
 * Created by isanechek on 11/17/17.
 */
abstract class BaseFragment : Fragment() {

    lateinit var navigator: Navigator
    protected lateinit var activity: BaseActivity

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is BaseActivity) {
            this.activity = context
            navigator = activity.getInstanceNavigator()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(layoutResId(), container, false)

    protected abstract fun layoutResId(): Int

    inline fun <reified T : Fragment> goTo(keepState: Boolean = true,
                                           withCustomAnimation: Boolean = false,
                                           arg: Bundle = Bundle.EMPTY) {
        navigator.goTo<T>(keepState = keepState, withCustomAnimation = withCustomAnimation, arg = arg)
    }

    open fun getTitle(): String = emptyString
}