package com.isanechek.wallpaper.utils.exception

class ServerIOException : ServerException {

    constructor() : super()

    constructor(message: String) : super(message)

    constructor(e: Throwable) : super(e)
}