package ru.isanechek.basemvp

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.os.Bundle


abstract class BaseMVPPresenter<V : BaseMVPContract.View> : LifecycleObserver, BaseMVPContract.Presenter<V> {

    private var sb: Bundle? = null
    private var v: V? = null

    override val stateBundle: Bundle = if (sb == null) Bundle() else sb!!

    override var view: V? = v

    override fun attachLifecycle(lifecycle: Lifecycle) {
        lifecycle.addObserver(this)
    }

    override fun detachLifecycle(lifecycle: Lifecycle) {
        lifecycle.removeObserver(this)
    }

    override fun attachView(view: V) {
        this.v = view
    }

    override fun detachView() {
        this.v = null
    }

    override val isViewAttached: Boolean = v != null


    override fun onPresenterCreate() {
    }

    override fun onPresenterDestroy() {
        if (sb != null && !sb!!.isEmpty) {
            sb!!.clear()
        }
    }
}