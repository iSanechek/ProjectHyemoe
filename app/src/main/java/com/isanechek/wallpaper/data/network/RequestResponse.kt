package com.isanechek.wallpaper.data.network

enum class Status {
    SUCCESS,
    ERROR,
    LOADING
}

data class Response<T>(val status: Status,
                       val data: T?,
                       val message: String?) {

    companion object {

        fun <T> success(data: T? = null, message: String?): Response<T> =
                Response(Status.SUCCESS, data, message)

        fun <T> error(data: T? = null, message: String): Response<T> =
                Response(Status.ERROR, data, message)

        fun <T> loading(data: T? = null, message: String?): Response<T> =
                Response(Status.LOADING, data, message)
    }
}