package com.isanechek.repository

@Suppress("DataClassPrivateConstructor")
data class NetworkState private constructor(
        val status: Status,
        val msg: String = "") {
    companion object {
        val LOADED = NetworkState(Status.SUCCESS)
        val LOADING = NetworkState(Status.RUNNING)
        val INITIAL = NetworkState(Status.INITIAL)
        val BAD_REQUEST = NetworkState(Status.BAD_REQUEST)
        fun error(msg: String = "", msgId: Int = 0) = NetworkState(Status.FAILED, msg)
    }
}