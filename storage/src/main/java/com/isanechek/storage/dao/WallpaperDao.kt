package com.isanechek.storage.dao

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.isanechek.storage.entity.WallpaperEntity

@Dao
interface WallpaperDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(wallpapers: List<WallpaperEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(wallpaper: WallpaperEntity)

    @Query("SELECT * FROM wallpapers WHERE category =:category ORDER BY last_update_data ASC")
    fun loadPaging(category: String): DataSource.Factory<Int, WallpaperEntity>

//    @Query("SELECT id FROM wallpapers WHERE category =:category")
//    fun loadIds(category: String): Set<Int>

    @Query("UPDATE wallpapers SET preview_url = :previewUrl WHERE id =:wallpaperId")
    fun updatePreviewUrl(wallpaperId: Int, previewUrl: String)

    @Query("DELETE FROM wallpapers WHERE id =:wallpaperId")
    fun removeId(wallpaperId: Int)

    @Query("DELETE FROM wallpapers WHERE id in (:ids)")
    fun removeIds(ids: Set<Int>)

    @Query("DELETE FROM wallpapers")
    fun removeAll()
}