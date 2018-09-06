package com.isanechek.wallpaper.update

import com.isanechek.common.DebugUtils
import com.isanechek.wallpaper.common.JSONParser
import com.isanechek.wallpaper.models.Update
import org.json.JSONException
import org.json.JSONObject

class JSONParserImpl(private val debugUtils: DebugUtils) : JSONParser<Update> {

    override fun parse(jsonObject: JSONObject): Update? {
        try {
            val info = jsonObject.getJSONObject("info")
            val version = info.getInt("versionCode")
            val url = info.getString("path")
            val notes = info.getString("notes")
            return null
        } catch (e: JSONException) {
            debugUtils.sendStackTrace(e, "The JSON updater file is mal-formatted.")
        }
        return null
    }
}