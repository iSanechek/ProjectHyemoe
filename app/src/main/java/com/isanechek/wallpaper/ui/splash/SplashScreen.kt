package com.isanechek.wallpaper.ui.splash

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.isanechek.common.DebugUtils
import com.isanechek.repository.Status.*
import com.isanechek.wallpaper.utils.RequestLimiter
import com.isanechek.extensions.delay
import com.isanechek.extensions.emptyString
import com.isanechek.wallpaper.ui.main.MainActivity
import kotlinx.android.synthetic.main.splash_screen_layout.*
import org.koin.android.ext.android.inject
import java.util.concurrent.TimeUnit

class SplashScreen : AppCompatActivity() {

    private val viewModel by inject<SplashViewModel>()
    private val debug by inject<DebugUtils>()
    private val requestLimiter = RequestLimiter<String>(
            6,
            TimeUnit.HOURS
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val preferences = viewModel.preferences
        val (key, _) = preferences.time
        if (!requestLimiter.shouldFetch(preferences, debug)) {
            debug.log("$TAG limiter true")
            if (key.isNotEmpty()) {
                startActivity(preferences.time)
                debug.log("$TAG start main")
            } else {
                debug.log("$TAG load data 1")
                setupAction()
            }
        } else {
            debug.log("$TAG load data 2")
            setupAction()
        }
    }

    private fun setupAction() {
        setContentView(_layout.splash_screen_layout)
        splash_progress.progressColor = _color.my_primary_dark_color
        splash_info_tv.text = "by AverdSoft"
        delay(500) { viewModel.loadData() }
        viewModel.state.observe(this, Observer { state ->
            state ?: return@Observer
            when(state.status) {
                RUNNING -> {
                    splash_progress.start()
                    splash_info_tv.text = getString(_string.splash_screen_check_update_title)
                }
                SUCCESS -> {
                    splash_progress.stop()
                    splash_info_tv.text = emptyString
                }
                FAILED -> {
                    splash_progress.stop()
                    splash_info_tv.text = emptyString
                    Toast.makeText(this@SplashScreen, state.msg, Toast.LENGTH_SHORT).show()
                }
                BAD_REQUEST -> TODO()
                NOT_FIND -> TODO()
                INITIAL -> TODO()
            }
        })

        viewModel.data.observe(this, Observer { data ->
            data ?: return@Observer
            delay(500) {
                startActivity(Pair(data.second.publicKey, data.second.path))
            }
        })
    }

    private fun startActivity(data: Pair<String, String>) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra(ARGS_KEY, data.component1())
        intent.putExtra(ARGS_PATH, data.component2())
        startActivity(intent)
    }
}

private const val TAG = "SplashScreen"
const val ARGS_PATH = "args.path"
const val ARGS_KEY = "args.key"