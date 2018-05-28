package com.isanechek.wallpaper.data.network

import com.isanechek.wallpaper.data.database.Category
import com.isanechek.wallpaper.data.database.Wallpaper
import com.isanechek.wallpaper.data.network.dto.Resource
import com.isanechek.wallpaper.utils.MaterialColorPalette
import com.isanechek.wallpaper.utils.extensions.emptyString

/**
 * Created by isanechek on 7/12/17.
 */
object MappingData {

    fun mappingResourceToWallpapers2(res: com.isanechek.wallpaper.data.network.dto.Resource, album: String) : List<Wallpaper> =
            res.resourceList.items.map { Wallpaper(
                    title = it.name,
                    publicKey = it.publicKey,
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


    fun mappingResourceToAlbum(resource: com.isanechek.wallpaper.data.network.dto.Resource?): List<Category> {
        val result = mutableListOf<Category>()
        val covers = mutableMapOf<String, String>()

        resource?.resourceList?.items?.forEach { item ->
            if (item.type == "file") {
                covers[item.name.replaceFirst(".jpg", "").trim()] = item.preview
            }
        }

        resource?.resourceList?.items?.forEach { item ->
            if(item.type == "dir") {
                val i = mappingItem(item, covers[item.name] ?: emptyString)
                result.add(i)
            }
        }
        return result
    }

    private fun mappingItem(resource: Resource, coverPath: String = emptyString): Category = Category(
            title = resource.name,
            publicKey = resource.publicKey,
            publicPath = resource.path,
            createDate = resource.created,
            lastUpdate = resource.modified,
            size = resource.size,
            color = MaterialColorPalette.randomColor(),
            cover = coverPath,
            newItem = false)
}