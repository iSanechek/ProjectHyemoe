package com.isanechek.storage.dao

import androidx.paging.DataSource
import androidx.room.*
import com.isanechek.storage.entity.CategoryEntity
import com.isanechek.storage.models.Container

@Dao
interface CategoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(categories: List<CategoryEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(category: CategoryEntity)

    @Query("SELECT * FROM categories ORDER BY last_update_data ASC")
    fun load(): DataSource.Factory<Int, CategoryEntity>

    @Query("SELECT * FROM categories ORDER BY last_update_data ASC")
    fun loadCategories(): List<CategoryEntity>

    @Query("SELECT id FROM categories")
    fun loadCategoriesIds(): List<Container>

    @Query("UPDATE categories SET preview_url = :previewUrl WHERE id =:categoryId")
    fun updatePreviewUrl(categoryId: String, previewUrl: String)

    @Query("DELETE FROM categories WHERE id =:categoryId")
    fun removeId(categoryId: String)

    @Query("DELETE FROM categories WHERE id in (:ids)")
    fun removeIds(ids: Set<String>)

    @Query("DELETE FROM categories")
    fun removeAll()

    @Query("SELECT count(*) FROM categories")
    fun getCategoriesSize(): Int

    @Transaction
    fun transaction(remove: Set<String>, update: List<CategoryEntity>, insert: List<CategoryEntity>) {
        removeIds(remove)
        update.map {
            updatePreviewUrl(it.id, it.previewUrl)
        }
        insert(insert)
    }
}