package com.isanechek.wallpaper.data.database

import android.arch.lifecycle.LiveData
import android.arch.paging.DataSource
import android.arch.persistence.room.*
import com.isanechek.wallpaper.utils.Const

/**
 * Created by isanechek on 9/15/17.
 */
@Dao
interface WallDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCategory(item: Category)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllCategory(items: List<Category>)

    @Query("SELECT * FROM ${Const.CATEGORY_TABLE_NAME} WHERE title = :arg0")
    fun loadCategory(arg0: String) : Category

    @Query("SELECT * FROM ${Const.CATEGORY_TABLE_NAME}")
    fun loadAllCategory(): List<Category>

    @Query("SELECT * FROM ${Const.CATEGORY_TABLE_NAME}")
    fun loadAllPagedCategory(): DataSource.Factory<Int, Category>

    @Update
    fun updateCategory(item: Category)

    @Delete
    fun removeCategory(item: Category)

    @Delete
    fun removeAllCategory(items: List<Category>)

    /*Wallpapers*/

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertWallpaper(item: Wallpaper)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllWallpapers(items: List<Wallpaper>)

    @Query("SELECT * FROM ${Const.WALLPAPER_TABLE_NAME} WHERE title = :arg0")
    fun loadWallpaperItem(arg0: String): Wallpaper

    @Query("SELECT * FROM ${Const.WALLPAPER_TABLE_NAME} WHERE title = :arg0")
    fun loadWallpaperLiveData(arg0: String): LiveData<Wallpaper>

    @Query("SELECT * FROM ${Const.WALLPAPER_TABLE_NAME} WHERE title = :arg0")
    fun loadWallpaper(arg0: String): Wallpaper

    @Query("SELECT * FROM ${Const.WALLPAPER_TABLE_NAME} WHERE category = :arg0")
    fun loadAllWallpapers(arg0: String) : LiveData<List<Wallpaper>>

    @Query("SELECT * FROM ${Const.WALLPAPER_TABLE_NAME} WHERE category = :arg0")
    fun loadAllWallpapersCategory(arg0: String) : List<Wallpaper>

    @Query("SELECT * FROM ${Const.WALLPAPER_TABLE_NAME} WHERE category = :arg0")
    fun loadAllPagedWallpapers(arg0: String) : DataSource.Factory<Int, Wallpaper>

    @Update
    fun updateWallpaper(item: Wallpaper)

    @Delete
    fun removeWallpaper(item: Wallpaper)

    @Delete
    fun removeAllWallpapers(items: List<Wallpaper>)

}