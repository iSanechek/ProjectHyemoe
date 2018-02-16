package com.isanechek.wallpaper.data.network

import com.isanechek.wallpaper.data.database.Category
import com.isanechek.wallpaper.data.database.Wallpaper
import com.isanechek.wallpaper.utils.MaterialColorPalette
import com.isanechek.wallpaper.utils.extensions.emptyString

/**
 * Created by isanechek on 7/12/17.
 */
object MappingData {

    fun mappingResourceToWallpapers2(res: com.isanechek.wallpaper.data.network.dto.Resource, album: String) : List<Wallpaper> =
            res.resourceList.items.map { Wallpaper(
                    title = it.name,
                    publicKey = it.name,
                    publicPath = it.path,
                    preview = it.preview,
                    cacheKey = emptyString,
                    fullCachePath = emptyString,
                    category = album,
                    author = emptyString,
                    createDate = it.created,
                    lastUpdate = it.modified,
                    size = it.size,
                    newItem = false) }.toList()

    fun mappingResourceToAlbum2(resource: com.isanechek.wallpaper.data.network.dto.Resource?): List<Category> =
            resource?.resourceList?.items?.map { Category(
                    title = it.name,
                    publicKey = it.publicKey,
                    publicPath = it.path,
                    createDate = it.created,
                    lastUpdate = it.modified,
                    size = it.size,
                    color = MaterialColorPalette.randomColor(),
                    newItem = false) }?.toList() ?: emptyList()
}