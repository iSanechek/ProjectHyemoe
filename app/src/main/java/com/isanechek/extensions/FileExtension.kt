package com.isanechek.extensions

import java.io.File
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*

fun File.copyFromInputStream(inputStream: InputStream) {
    inputStream.use { input ->
        this.outputStream().use { output ->
            input.copyTo(output)
        }
    }
}

fun String.isImageSupport(): Boolean =
        this.equals("image/gif", true) ||
        this.equals("image/jpeg", true) ||
        this.equals("image/png", true) ||
        this.equals("image/jpg", true)

fun File.createNoMediaDir() {
    if (!this.exists()) {
        this.mkdirs()
    }
    val no = File(this, ".nomedia")
    if (!no.exists()) {
        no.createNewFile()
    }
}

fun File.createImageTemp(prefix: String? = null, type: String? = null): File {
    val time = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
    return if (prefix != null) {
        createTempFile("${prefix}_IMAGE_$time", type ?: ".jpg")
    } else {
        createTempFile("IMAGE_$time", type ?: ".jpg")
    }
}