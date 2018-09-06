package com.isanechek.repository.category

import com.isanechek.common.DebugUtils
import com.isanechek.common.models.Category
import com.isanechek.common.models.Folder
import com.isanechek.common.models.Root
import com.isanechek.extensions.emptyString
import com.isanechek.storage.entity.CategoryEntity
import com.isanechek.wallpaper.data.network.dto.Resource

/**
 *  Писал норкаман под дикой дозой. Нужно переписать!.
 */

object CategoriesMapping {

    @JvmStatic
    fun mapRoot(resource: Resource?): Root {
        resource ?: return Root(emptyString, emptyString, emptyString)
        val jsonUrl = resource
                .resourceList
                .items
                .first {
                    it.type.equals("file", ignoreCase = true)
                }.file

        val root = resource
                .resourceList
                .items
                .first {
                    it.type.equals("dir", ignoreCase = true)
                }
        return Root(
                jsonUrl = jsonUrl,
                key = root.publicKey,
                path = root.path)
    }

    @JvmStatic
    fun switchMapFolder(resource: Resource?) : List<Folder> {
        resource ?: return emptyList()
        val res = resource.resourceList
        val offset = res.offset
        return res.items
                .map {
                    Folder(key = it.publicKey,
                            path = it.path,
                            offset = offset,
                            name = it.name,
                            created = it.created,
                            modified = it.modified,
                            type = it.mediaType)
                }.toList()
    }

    @JvmStatic
    fun mapToCategory(folder: Folder, resource: Resource?, refresh: Boolean, debug: DebugUtils): CategoryEntity {
        val res = resource
                ?.resourceList
                ?.items
                ?.first {
                    it.type.equals("dir", ignoreCase = true)
                }

//        debug.log(message = "mapToCategory key ${res?.publicKey}")
//        debug.log(message = "mapToCategory path ${res?.path}")
//        debug.log(message = "mapToCategory name ${res?.name}")

        val preview = resource
                ?.resourceList
                ?.items
                ?.first {
                    it.type.equals("file", ignoreCase = true)
                }
                ?.preview
                ?: emptyString

//        debug.log("mapToCategory preview $preview")

        return CategoryEntity(
                title = folder.name,
                publicKey = res?.publicKey ?: emptyString,
                publicPath = res?.path ?: emptyString,
                previewUrl = preview,
                lastUpdateDate = res?.modified ?: emptyString,
                id = res?.id ?: folder.name)

    }



}