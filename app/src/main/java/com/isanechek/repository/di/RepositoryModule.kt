package com.isanechek.repository.di

import com.isanechek.common.models.Category
import com.isanechek.common.models.Wallpaper
import com.isanechek.repository.Repository
import com.isanechek.repository.category.CategoryRepository
import com.isanechek.repository.main.MainRepository
import com.isanechek.repository.main.MainRepositoryImpl
import com.isanechek.repository.wallpapers.WallpapersRepository
import org.koin.dsl.module.module

val repositoryModule = module {

    single(name = CATEGORY_REPO) {
        CategoryRepository(
                get(),
                get(),
                get()) as Repository<Category>
    }

    single(name = WALLPAPERS_REPO) {
        WallpapersRepository(
                get(),
                get()) as Repository<Wallpaper>
    }

    single {
        MainRepositoryImpl(
                get(),
                get(),
                get(),
                get()) as MainRepository
    }
}

const val WALLPAPERS_REPO = "wallpapers.repo"
const val CATEGORY_REPO = "category.repo"