package ru.isanechek.basemvp

import android.arch.lifecycle.Lifecycle
import android.os.Bundle


interface BaseMVPContract {

    interface View

    interface Presenter<V : BaseMVPContract.View> {

        val stateBundle: Bundle

        val view: V?

        val isViewAttached: Boolean

        fun attachLifecycle(lifecycle: Lifecycle)

        fun detachLifecycle(lifecycle: Lifecycle)

        fun attachView(view: V)

        fun detachView()

        fun onPresenterCreate()

        fun onPresenterDestroy()
    }
}