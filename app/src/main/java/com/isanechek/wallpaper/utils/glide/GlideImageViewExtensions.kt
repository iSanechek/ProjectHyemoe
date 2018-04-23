package com.isanechek.wallpaper.utils.glide

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.Transformation
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import jp.wasabeef.glide.transformations.BlurTransformation
import jp.wasabeef.glide.transformations.ColorFilterTransformation

private const val DEFAULT_DURATION_MS = 200

enum class TransformationType {
    CIRCLE,
    ROUND,
    ROUND_WITH_FILTER,
    FILTER,
    FILTER_AND_BLUR,
    BLUR,
    BLUR_25,
    NOTHING;

    fun getTransformation(): Transformation<Bitmap> = when (this) {
        CIRCLE -> CircleCrop()
        ROUND -> RoundedCorners(20)
        FILTER -> ColorFilterTransformation(Color.argb(75, 76, 175, 80))
        FILTER_AND_BLUR -> MultiTransformation(BlurTransformation(50),
                ColorFilterTransformation(Color.argb(80, 76, 175, 80)))
        BLUR -> BlurTransformation(30)
        BLUR_25 -> BlurTransformation(99)
        ROUND_WITH_FILTER -> MultiTransformation(RoundedCorners(8),
                ColorFilterTransformation(Color.argb(155, 0, 0, 0)))
        else -> {
            TODO()
        }
    }
}

@JvmName("privateLoad")
private fun load(view: ImageView,
                 url: String?,
                 transformationType: TransformationType = TransformationType.NOTHING) {

    val request: GlideRequest<Drawable> = GlideApp.with(view.context)
            .load(url)
            .transition(DrawableTransitionOptions.withCrossFade(DEFAULT_DURATION_MS))
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)

    if (transformationType != TransformationType.NOTHING) {
        request.transform(transformationType.getTransformation())
    }

    request.into(view)
}

fun ImageView.clear() {
    GlideApp.with(context).clear(this)
}

fun ImageView.load(url: String?) {
    load(this, url)
}

fun ImageView.load(url: String?, transformationType: TransformationType) {
    load(this, url, transformationType)
}