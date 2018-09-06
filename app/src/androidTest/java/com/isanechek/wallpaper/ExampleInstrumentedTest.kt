package com.isanechek.wallpaper

import androidx.test.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Instrumented com.hanks.htextview.test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under com.hanks.htextview.test.
        val appContext = InstrumentationRegistry.getTargetContext()
        assertEquals("com.isanechek.wallpaper", appContext.packageName)
    }
}
