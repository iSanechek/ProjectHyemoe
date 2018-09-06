package com.isanechek.wallpaper.utils.impl

import com.isanechek.common.AppExecutors
import com.isanechek.wallpaper.utils.MainThreadExecutor
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class AppExecutorsImpl : AppExecutors {

    override fun diskIO(): Executor = Executors.newSingleThreadExecutor()

    override fun networkIO(): Executor = Executors.newFixedThreadPool(3)

    override fun mainThread(): Executor = MainThreadExecutor()

}