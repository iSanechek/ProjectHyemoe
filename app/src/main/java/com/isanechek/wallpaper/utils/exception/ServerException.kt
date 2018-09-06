package com.isanechek.wallpaper.utils.exception

abstract class ServerException : Exception {

    constructor() : super()

    constructor(detailMessage: String) : super(detailMessage)

    constructor(detailMessage: String, throwable: Throwable) : super(detailMessage, throwable)

    constructor(throwable: Throwable) : super(throwable)
}