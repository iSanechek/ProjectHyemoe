package com.isanechek.common

interface DebugUtils {
    fun sendStackTrace(exception: Exception?, message: String = "DefaultMessageException")
    fun sendStackTrace(exception: Throwable?, message: String = "DefaultMessageException")
    fun log(message: String?)
    fun log(items: List<Any>, message: String = "")
}