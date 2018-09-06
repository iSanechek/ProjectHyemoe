package com.isanechek.repository

data class Response<T>(val status: Status,
                       val data: T?,
                       val message: String?) {

    companion object {

        fun <T> success(data: T?): Response<T> =
                Response(Status.SUCCESS, data, null)

        fun <T> error(msg: String): Response<T> =
                Response(Status.FAILED, null, msg)

        fun <T> loading(): Response<T> =
                Response(Status.RUNNING, null, null)
    }
}