package com.isanechek.wallpaper.update

import android.app.ProgressDialog
import android.content.Context
import com.isanechek.common.DebugUtils
import com.isanechek.wallpaper.common.DownloadHelper
import com.isanechek.wallpaper.common.JSONParser
import com.isanechek.wallpaper.common.UpdateAppHelper
import com.isanechek.repository.Response
import com.isanechek.wallpaper.models.Update
import com.isanechek.wallpaper.utils.UpdaterUtils
import com.isanechek.wallpaper.utils.SingleLiveEvent
import com.isanechek.extensions.emptyString
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.withContext
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.IOException

class UpdateAppHelperImpl(private val debug: DebugUtils,
                          private val client: OkHttpClient,
                          private val parser: JSONParser<Update>,
                          private val context: Context,
                          private val downloadHelper: DownloadHelper) : UpdateAppHelper {

    private  val status = SingleLiveEvent<Response<Update>>()

    override fun status(): SingleLiveEvent<Response<Update>> = status

    override suspend fun checkUpdate() = withContext(CommonPool) {
        requestUpdate()
    }

    override suspend fun checkUpdateNow() = withContext(CommonPool) {
        requestUpdate()
    }

    override suspend fun updateApk(ctx: Context, apkUrl: String) {
        val progressDialog = ProgressDialog(ctx)
        progressDialog.setMessage("Update....")
        progressDialog.isIndeterminate = true
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progressDialog.setCancelable(false)
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.show()
        val path = withContext(CommonPool) {
            downloadHelper.downloadApk(apkUrl)
        }

        if (progressDialog.isShowing) progressDialog.dismiss()
        UpdaterUtils.installApkAsFile(context, File(path))
    }

    override fun remindLater() {

    }

    private fun requestUpdate() {
        status.postValue(Response.loading())
        val request = Request.Builder()
                .url(UPDATE_URL)
                .build()
        try {
            val response = client.newCall(request).execute().body()
            val update = parser.parse(JSONObject(response!!.string()))
            val oldVersion = UpdaterUtils.appVersion(context)
            if (UpdaterUtils.isUpdateAvailable(oldVersion, update!!.version)) {
                status.postValue(Response.success(update))
            } else {
                status.postValue(Response.success(null))
            }
        } catch (e: IOException) {
            status.postValue(Response.error(e.message?:"IOException"))
            debug.sendStackTrace(e, e.message ?: emptyString)
        } catch (e: JSONException) {
            status.postValue(Response.error(e.message?:"JSONException"))
            debug.sendStackTrace(e, e.message ?: emptyString)
        }
    }


    companion object {
        private const val UPDATE_URL = "https://raw.githubusercontent.com/iSanechek/ProjectHyemoe/master/update.json"
    }

}