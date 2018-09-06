package com.isanechek.wallpaper.common

import org.json.JSONObject

interface JSONParser<T> {
    fun parse(jsonObject: JSONObject): T?
}