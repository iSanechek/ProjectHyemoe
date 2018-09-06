package com.isanechek.wallpaper.data.pref

import android.content.SharedPreferences
import androidx.core.content.edit
import com.isanechek.wallpaper.DEFAULT_ALBUM_KEY
import com.isanechek.wallpaper.common.PrefManager
import com.isanechek.wallpaper.models.Update
import com.isanechek.extensions.emptyString

class PrefManagerImpl(private val preferences: SharedPreferences) : PrefManager {

    override var lastUpdateData: Long
        get() = preferences.getLong("last.update.data", System.currentTimeMillis())
        set(value) {
            preferences.edit {
                putLong("last.update.data", value)
            }
        }

    override var updateTime: Long
        get() = preferences.getLong("update.time", 0)
        set(value) {
            preferences.edit {
                putLong("update.time", value)
            }
        }

    override var time: Pair<String, String>
        get() = Pair(
                first = preferences.getString("time.key", emptyString),
                second = preferences.getString("time.path", emptyString))
        set(value) {
            preferences.edit {
                putString("time.key", value.first)
                putString("time.path", value.second)
            }
        }

    override var update: Update
        get() = Update(
                version = preferences.getInt("wallpapers.version.code", 0),
                messag = preferences.getString("wallpapers.url.apk", emptyString) ?: emptyString,
                notes = preferences.getString("wallpapers.notes", emptyString) ?: emptyString,
                url = preferences.getString("wallpapers.message", emptyString) ?: emptyString)
        set(value) {
            preferences.edit {
                putInt("wallpapers.version.code", value.version)
                putString("wallpapers.url.apk", value.url)
                putString("wallpapers.notes", value.notes)
                putString("wallpapers.message", value.messag)
            }
        }

    override var defaultAlbumKey: String
        get() = preferences.getString("default.key_wallpapers", DEFAULT_ALBUM_KEY) ?: emptyString
        set(value) {
            preferences.edit {
                putString("default.key_wallpapers", value)
            }
        }

}