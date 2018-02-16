//package com.isanechek.wallpaper.utils.glide
//
//import android.graphics.Bitmap
//import android.graphics.drawable.Drawable
//import android.widget.ImageView
//import com.bumptech.glide.load.Transformation
//import com.bumptech.glide.load.engine.DiskCacheStrategy
//import com.bumptech.glide.load.resource.bitmap.CircleCrop
//import com.bumptech.glide.load.resource.bitmap.RoundedCorners
//import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
//
//private val DEFAULT_DURATION_MS = 200
//
//enum class TransformationType {
//    CIRCLE,
//    ROUND,
//    NOTHING;
//
//    fun getTransformation(): Transformation<Bitmap> = when (this) {
//        CIRCLE -> CircleCrop()
//        ROUND -> RoundedCorners(20)
//        else -> {
//            TODO()
//        }
//    }
//}
//
//@JvmName("privateLoad")
//private fun load(view: ImageView,
//                 url: String?,
//                 transformationType: TransformationType = TransformationType.NOTHING) {
//
//    val request: GlideRequest<Drawable> = GlideApp.with(view.context)
//            .load(url)
//            .transition(DrawableTransitionOptions.withCrossFade(DEFAULT_DURATION_MS))
//            .centerCrop()
//            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
//
//    if (transformationType != TransformationType.NOTHING) {
//        request.transform(transformationType.getTransformation())
//    }
//
//    request.into(view)
//}
//
//fun ImageView.clear() {
//    GlideApp.with(context).clear(this)
//}
//
//fun ImageView.load(url: String?) {
//    load(this, url)
//}
//
//fun ImageView.load(url: String?, transformationType: TransformationType) {
//    load(this, url, transformationType)
//}