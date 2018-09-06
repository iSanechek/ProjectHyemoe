package com.isanechek.repository.wallpapers

import com.isanechek.common.models.Wallpaper
import com.isanechek.wallpaper.data.network.dto.Resource

object WallpaperMapping {

    fun map(resource: List<Resource>, category: String): List<Wallpaper> {
        val temp = mutableListOf<Wallpaper>()
        for(res in resource) {
            temp.add(Wallpaper(
                    id = res.id,
                    publicKey = res.publicKey,
                    title = res.name,
                    publicPath = res.publicUrl,
                    previewUrl = res.preview,
                    downloadUrl = res.file,
                    lastUpdateDate = res.modified,
                    category = category,
                    size = res.size
            ))
        }
        return temp
    }
}