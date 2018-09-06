package com.isanechek.common

import java.util.concurrent.Executor

interface AppExecutors {
    fun diskIO(): Executor
    fun networkIO(): Executor
    fun mainThread(): Executor
}