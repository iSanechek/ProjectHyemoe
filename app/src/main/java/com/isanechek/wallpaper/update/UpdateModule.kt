package com.isanechek.wallpaper.update

import com.isanechek.wallpaper.common.JSONParser
import com.isanechek.wallpaper.common.UpdateAppHelper
import com.isanechek.wallpaper.models.Update
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module.module

val updateModule = module {

    single {
        JSONParserImpl(get()) as JSONParser<Update>
    }


    single {
        UpdateAppHelperImpl(
                get(),
                get(),
                get(),
                androidContext(),
                get()) as UpdateAppHelper
    }
}