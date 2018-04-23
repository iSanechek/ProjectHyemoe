package com.isanechek.wallpaper.view.test

import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.isanechek.network.Album
import com.isanechek.network.Api
import com.isanechek.network.ResponseInfo

/**
 * Created by isanechek on 2/27/18.
 */
class TestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        testLog("Start")
        Task().execute()
    }

    class Task : AsyncTask<Unit, Unit, Unit>() {
        override fun doInBackground(vararg params: Unit?) {
            val api = Api()
            api.getAlbums("https://yadi.sk/d/OPeqGfrb3TnF5y", object : Api.ApiCallback<Any> {
                override fun loading(data: Any?) {
                    Log.e("TEST", "loading")
                }

                override fun success(data: Any) {
                    Log.e("TEST", "success")
                    if (data is Pair<*,*>) {
                        val f = data.first as ResponseInfo
                        val s = data.second as List<Album>

                        Log.e("TEST", "info ${f.name}")
                        Log.e("TEST", "size ${s.size}")

                        s.forEach { item ->
                            Log.e("TEST", "Item name ${item.name}")
                            Log.e("TEST", "Item name ${item.previewUrl}")
                            Log.e("TEST", "Item name ${item.size}")
                        }


                    }
                }

                override fun error(message: String?) {
                    Log.e("TEST", "Error $message")
                }

            })
        }
    }

    private fun testLog(message: String) {
        Log.e("TEST", message)
    }
}