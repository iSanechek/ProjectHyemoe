package com.isanechek.wallpaper.ui.widgets

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.content.res.Resources.NotFoundException
import android.graphics.*
import android.graphics.Bitmap.Config
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.net.Uri
import android.util.AttributeSet
import android.util.Log
import android.widget.ImageView
import com.isanechek.wallpaper.R

class SelectableRoundedImageView : ImageView {

    private val MAX_PARALLAX_OFFSET = context.resources.getDimension(R.dimen.parallax_image_view_offset).toInt()

    private var recyclerView_height = -1
    private val recyclerView_location = intArrayOf(-1, -1)

    internal var attached = false

    private var mResource = 0

    // Set default scale type to FIT_CENTER, which is default scale type of
    // original ImageView.
    private var mScaleType: ImageView.ScaleType = ImageView.ScaleType.FIT_CENTER

    var cornerRadius = 0.0f
    private var mRightTopCornerRadius = 0.0f
    private var mLeftBottomCornerRadius = 0.0f
    private var mRightBottomCornerRadius = 0.0f

    var borderWidth = 0.0f
        private set
    var borderColors: ColorStateList? = ColorStateList.valueOf(DEFAULT_BORDER_COLOR)
        private set

    private var isOval = false

    private var mDrawable: Drawable? = null

    private var mRadii = floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)

    var borderColor: Int
        get() = borderColors!!.defaultColor
        set(color) = setBorderColor(ColorStateList.valueOf(color))

    constructor(context: Context) : super(context) {}

    @JvmOverloads constructor(context: Context, attrs: AttributeSet, defStyle: Int = 0) : super(context, attrs, defStyle) {

        val a = context.obtainStyledAttributes(attrs,
                R.styleable.SelectableRoundedImageView, defStyle, 0)

        val index = a.getInt(R.styleable.SelectableRoundedImageView_android_scaleType, -1)
        if (index >= 0) {
            scaleType = sScaleTypeArray[index]
        }

        cornerRadius = a.getDimensionPixelSize(
                R.styleable.SelectableRoundedImageView_sriv_left_top_corner_radius, 0).toFloat()
        mRightTopCornerRadius = a.getDimensionPixelSize(
                R.styleable.SelectableRoundedImageView_sriv_right_top_corner_radius, 0).toFloat()
        mLeftBottomCornerRadius = a.getDimensionPixelSize(
                R.styleable.SelectableRoundedImageView_sriv_left_bottom_corner_radius, 0).toFloat()
        mRightBottomCornerRadius = a.getDimensionPixelSize(
                R.styleable.SelectableRoundedImageView_sriv_right_bottom_corner_radius, 0).toFloat()

        if (cornerRadius < 0.0f || mRightTopCornerRadius < 0.0f
                || mLeftBottomCornerRadius < 0.0f || mRightBottomCornerRadius < 0.0f) {
            throw IllegalArgumentException("radius values cannot be negative.")
        }

        mRadii = floatArrayOf(cornerRadius, cornerRadius, mRightTopCornerRadius, mRightTopCornerRadius, mRightBottomCornerRadius, mRightBottomCornerRadius, mLeftBottomCornerRadius, mLeftBottomCornerRadius)

        borderWidth = a.getDimensionPixelSize(
                R.styleable.SelectableRoundedImageView_sriv_border_width, 0).toFloat()
        if (borderWidth < 0) {
            throw IllegalArgumentException("border width cannot be negative.")
        }

        borderColors = a
                .getColorStateList(R.styleable.SelectableRoundedImageView_sriv_border_color)
        if (borderColors == null) {
            borderColors = ColorStateList.valueOf(DEFAULT_BORDER_COLOR)
        }

        isOval = a.getBoolean(R.styleable.SelectableRoundedImageView_sriv_oval, false)
        a.recycle()

        updateDrawable()
    }

    override fun drawableStateChanged() {
        super.drawableStateChanged()
        invalidate()
    }

    override fun getScaleType(): ImageView.ScaleType {
        return mScaleType
    }

    override fun setScaleType(scaleType: ImageView.ScaleType) {
        super.setScaleType(scaleType)
        mScaleType = scaleType
        updateDrawable()
    }

    override fun setImageDrawable(drawable: Drawable?) {
        mResource = 0
        mDrawable = SelectableRoundedCornerDrawable.fromDrawable(drawable, resources)
        super.setImageDrawable(mDrawable)
        updateDrawable()
    }

    override fun setImageBitmap(bm: Bitmap?) {
        mResource = 0
        mDrawable = SelectableRoundedCornerDrawable.fromBitmap(bm, resources)
        super.setImageDrawable(mDrawable)
        updateDrawable()
    }

    override fun setImageResource(resId: Int) {
        if (mResource != resId) {
            mResource = resId
            mDrawable = resolveResource()
            super.setImageDrawable(mDrawable)
            updateDrawable()
        }
    }

    override fun setImageURI(uri: Uri?) {
        super.setImageURI(uri)
        setImageDrawable(drawable)
    }

//    override fun onAttachedToWindow() {
//        super.onAttachedToWindow()
//        attached = true
//
//        val view = rootView.findViewWithTag<View>(ParallaxImageView.RECYCLER_VIEW_TAG)
//        if (view is RecyclerView) {
//            view.addOnScrollListener(object : RecyclerView.OnScrollListener() {
//                override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
//                    super.onScrolled(recyclerView, dx, dy)
//
//                    if (!attached) {
//                        recyclerView!!.removeOnScrollListener(this)
//                        return
//                    }
//
//                    if (recyclerView_height == -1) {
//                        recyclerView_height = recyclerView!!.height
//                        recyclerView.getLocationOnScreen(recyclerView_location)
//                    }
//
//                    setParallaxTranslation()
//                }
//            })
//        }

//    }

//    fun setParallaxTranslation() {
//        if (recyclerView_height == -1) {
//            return
//        }
//
//        val location = IntArray(2)
//        getLocationOnScreen(location)
//
//        val visible = location[1] + height > recyclerView_location[1] || location[1] < recyclerView_location[1] + recyclerView_height
//
//        if (!visible) {
//            return
//        }
//
//        val dy = (location[1] - recyclerView_location[1]).toFloat()
//
//        val translationY = MAX_PARALLAX_OFFSET * dy / recyclerView_height.toFloat()
//        setTranslationY(-translationY)
//    }

    private fun resolveResource(): Drawable? {
        val rsrc = resources ?: return null

        var d: Drawable? = null

        if (mResource != 0) {
            try {
                d = rsrc.getDrawable(mResource)
            } catch (e: NotFoundException) {
                Log.w(TAG, "Unable to find resource: " + mResource, e)
                // Don't try again.
                mResource = 0
            }

        }
        return SelectableRoundedCornerDrawable.fromDrawable(d, resources)
    }

    private fun updateDrawable() {
        if (mDrawable == null) {
            return
        }

        (mDrawable as SelectableRoundedCornerDrawable).scaleType = mScaleType
        (mDrawable as SelectableRoundedCornerDrawable).setCornerRadii(mRadii)
        (mDrawable as SelectableRoundedCornerDrawable).borderWidth = borderWidth
        (mDrawable as SelectableRoundedCornerDrawable).setBorderColor(borderColors)
        (mDrawable as SelectableRoundedCornerDrawable).isOval = isOval
    }

    /**
     * Set radii for each corner.
     *
     * @param leftTop The desired radius for left-top corner in dip.
     * @param rightTop The desired desired radius for right-top corner in dip.
     * @param leftBottom The desired radius for left-bottom corner in dip.
     * @param rightBottom The desired radius for right-bottom corner in dip.
     */
    fun setCornerRadiiDP(leftTop: Float, rightTop: Float, leftBottom: Float, rightBottom: Float) {
        val density = resources.displayMetrics.density

        val lt = leftTop * density
        val rt = rightTop * density
        val lb = leftBottom * density
        val rb = rightBottom * density

        mRadii = floatArrayOf(lt, lt, rt, rt, rb, rb, lb, lb)
        updateDrawable()
    }

    /**
     * Set border width.
     *
     * @param width
     * The desired width in dip.
     */
    fun setBorderWidthDP(width: Float) {
        val scaledWidth = resources.displayMetrics.density * width
        if (borderWidth == scaledWidth) {
            return
        }

        borderWidth = scaledWidth
        updateDrawable()
        invalidate()
    }

    fun setBorderColor(colors: ColorStateList?) {
        if (borderColors == colors) {
            return
        }

        borderColors = colors ?: ColorStateList
                .valueOf(DEFAULT_BORDER_COLOR)
        updateDrawable()
        if (borderWidth > 0) {
            invalidate()
        }
    }

    fun isOval(): Boolean {
        return isOval
    }

    fun setOval(oval: Boolean) {
        isOval = oval
        updateDrawable()
        invalidate()
    }

    internal class SelectableRoundedCornerDrawable(private val mBitmap: Bitmap?, r: Resources) : Drawable() {

        private val mBounds = RectF()
        private val mBorderBounds = RectF()

        private val mBitmapRect = RectF()
        private val mBitmapWidth: Int
        private val mBitmapHeight: Int

        private val mBitmapPaint: Paint
        private val mBorderPaint: Paint

        private val mBitmapShader: BitmapShader

        private val mRadii = floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
        private val mBorderRadii = floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)

        var isOval = false

        private var mBorderWidth = 0f
        var borderColors = ColorStateList.valueOf(DEFAULT_BORDER_COLOR)
            private set
        // Set default scale type to FIT_CENTER, which is default scale type of
        // original ImageView.
        var scaleType: ImageView.ScaleType? = ImageView.ScaleType.FIT_CENTER
            set(scaleType) {
                if (scaleType == null) {
                    return
                }
                field = scaleType
            }

        private val mPath = Path()
        private var mBoundsConfigured = false

        var borderWidth: Float
            get() = mBorderWidth
            set(width) {
                mBorderWidth = width
                mBorderPaint.strokeWidth = width
            }

        var borderColor: Int
            get() = borderColors.defaultColor
            set(color) = setBorderColor(ColorStateList.valueOf(color))

        init {
            mBitmapShader = BitmapShader(mBitmap!!, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)

            if (mBitmap != null) {
                mBitmapWidth = mBitmap.getScaledWidth(r.displayMetrics)
                mBitmapHeight = mBitmap.getScaledHeight(r.displayMetrics)
            } else {
                mBitmapHeight = -1
                mBitmapWidth = mBitmapHeight
            }

            mBitmapRect.set(0f, 0f, mBitmapWidth.toFloat(), mBitmapHeight.toFloat())

            mBitmapPaint = Paint(Paint.ANTI_ALIAS_FLAG)
            mBitmapPaint.style = Paint.Style.FILL
            mBitmapPaint.shader = mBitmapShader

            mBorderPaint = Paint(Paint.ANTI_ALIAS_FLAG)
            mBorderPaint.style = Paint.Style.STROKE
            mBorderPaint.color = borderColors.getColorForState(state, DEFAULT_BORDER_COLOR)
            mBorderPaint.strokeWidth = mBorderWidth
        }

        override fun isStateful(): Boolean {
            return borderColors.isStateful
        }

        override fun onStateChange(state: IntArray): Boolean {
            val newColor = borderColors.getColorForState(state, 0)
            if (mBorderPaint.color != newColor) {
                mBorderPaint.color = newColor
                return true
            } else {
                return super.onStateChange(state)
            }
        }

        private fun configureBounds(canvas: Canvas) {
            // I have discovered a truly marvelous explanation of this,
            // which this comment space is too narrow to contain. :)
            // If you want to understand what's going on here,
            // See http://www.joooooooooonhokim.com/?p=289
            val clipBounds = canvas.clipBounds
            val canvasMatrix = canvas.matrix

            if (ImageView.ScaleType.CENTER == scaleType) {
                mBounds.set(clipBounds)
            } else if (ImageView.ScaleType.CENTER_CROP == scaleType) {
                applyScaleToRadii(canvasMatrix)
                mBounds.set(clipBounds)
            } else if (ImageView.ScaleType.FIT_XY == scaleType) {
                val m = Matrix()
                m.setRectToRect(mBitmapRect, RectF(clipBounds), Matrix.ScaleToFit.FILL)
                mBitmapShader.setLocalMatrix(m)
                mBounds.set(clipBounds)
            } else if (ImageView.ScaleType.FIT_START == scaleType || ImageView.ScaleType.FIT_END == scaleType
                    || ImageView.ScaleType.FIT_CENTER == scaleType || ImageView.ScaleType.CENTER_INSIDE == scaleType) {
                applyScaleToRadii(canvasMatrix)
                mBounds.set(mBitmapRect)
            } else if (ImageView.ScaleType.MATRIX == scaleType) {
                applyScaleToRadii(canvasMatrix)
                mBounds.set(mBitmapRect)
            }
        }

        private fun applyScaleToRadii(m: Matrix) {
            val values = FloatArray(9)
            m.getValues(values)
            for (i in mRadii.indices) {
                mRadii[i] = mRadii[i] / values[0]
            }
        }

        private fun adjustCanvasForBorder(canvas: Canvas) {
            val canvasMatrix = canvas.matrix
            val values = FloatArray(9)
            canvasMatrix.getValues(values)

            val scaleFactorX = values[0]
            val scaleFactorY = values[4]
            val translateX = values[2]
            val translateY = values[5]

            val newScaleX = mBounds.width() / (mBounds.width() + mBorderWidth + mBorderWidth)
            val newScaleY = mBounds.height() / (mBounds.height() + mBorderWidth + mBorderWidth)

            canvas.scale(newScaleX, newScaleY)
            if (ImageView.ScaleType.FIT_START == scaleType || ImageView.ScaleType.FIT_END == scaleType
                    || ImageView.ScaleType.FIT_XY == scaleType || ImageView.ScaleType.FIT_CENTER == scaleType
                    || ImageView.ScaleType.CENTER_INSIDE == scaleType || ImageView.ScaleType.MATRIX == scaleType) {
                canvas.translate(mBorderWidth, mBorderWidth)
            } else if (ImageView.ScaleType.CENTER == scaleType || ImageView.ScaleType.CENTER_CROP == scaleType) {
                // First, make translate values to 0
                canvas.translate(
                        -translateX / (newScaleX * scaleFactorX),
                        -translateY / (newScaleY * scaleFactorY))
                // Then, set the final translate values.
                canvas.translate(-(mBounds.left - mBorderWidth), -(mBounds.top - mBorderWidth))
            }
        }

        private fun adjustBorderWidthAndBorderBounds(canvas: Canvas) {
            val canvasMatrix = canvas.matrix
            val values = FloatArray(9)
            canvasMatrix.getValues(values)

            val scaleFactor = values[0]

            val viewWidth = mBounds.width() * scaleFactor
            mBorderWidth = mBorderWidth * mBounds.width() / (viewWidth - 2 * mBorderWidth)
            mBorderPaint.strokeWidth = mBorderWidth

            mBorderBounds.set(mBounds)
            mBorderBounds.inset(-mBorderWidth / 2, -mBorderWidth / 2)
        }

        private fun setBorderRadii() {
            for (i in mRadii.indices) {
                if (mRadii[i] > 0) {
                    mBorderRadii[i] = mRadii[i]
                    mRadii[i] = mRadii[i] - mBorderWidth
                }
            }
        }

        override fun draw(canvas: Canvas) {
            canvas.save()
            if (!mBoundsConfigured) {
                configureBounds(canvas)
                if (mBorderWidth > 0) {
                    adjustBorderWidthAndBorderBounds(canvas)
                    setBorderRadii()
                }
                mBoundsConfigured = true
            }

            if (this.isOval) {
                if (mBorderWidth > 0) {
                    adjustCanvasForBorder(canvas)
                    mPath.addOval(mBounds, Path.Direction.CW)
                    canvas.drawPath(mPath, mBitmapPaint)
                    mPath.reset()
                    mPath.addOval(mBorderBounds, Path.Direction.CW)
                    canvas.drawPath(mPath, mBorderPaint)
                } else {
                    mPath.addOval(mBounds, Path.Direction.CW)
                    canvas.drawPath(mPath, mBitmapPaint)
                }
            } else {
                if (mBorderWidth > 0) {
                    adjustCanvasForBorder(canvas)
                    mPath.addRoundRect(mBounds, mRadii, Path.Direction.CW)
                    canvas.drawPath(mPath, mBitmapPaint)
                    mPath.reset()
                    mPath.addRoundRect(mBorderBounds, mBorderRadii, Path.Direction.CW)
                    canvas.drawPath(mPath, mBorderPaint)
                } else {
                    mPath.addRoundRect(mBounds, mRadii, Path.Direction.CW)
                    canvas.drawPath(mPath, mBitmapPaint)
                }
            }
            canvas.restore()
        }

        fun setCornerRadii(radii: FloatArray?) {
            if (radii == null)
                return

            if (radii.size != 8) {
                throw ArrayIndexOutOfBoundsException("radii[] needs 8 values")
            }

            for (i in radii.indices) {
                mRadii[i] = radii[i]
            }
        }

        override fun getOpacity(): Int {
            return if (mBitmap == null || mBitmap.hasAlpha() || mBitmapPaint.alpha < 255)
                PixelFormat.TRANSLUCENT
            else
                PixelFormat.OPAQUE
        }

        override fun setAlpha(alpha: Int) {
            mBitmapPaint.alpha = alpha
            invalidateSelf()
        }

        override fun setColorFilter(cf: ColorFilter?) {
            mBitmapPaint.colorFilter = cf
            invalidateSelf()
        }

        override fun setDither(dither: Boolean) {
            mBitmapPaint.isDither = dither
            invalidateSelf()
        }

        override fun setFilterBitmap(filter: Boolean) {
            mBitmapPaint.isFilterBitmap = filter
            invalidateSelf()
        }

        override fun getIntrinsicWidth(): Int {
            return mBitmapWidth
        }

        override fun getIntrinsicHeight(): Int {
            return mBitmapHeight
        }

        /**
         * Controls border color of this ImageView.
         *
         * @param colors
         * The desired border color. If it's null, no border will be
         * drawn.
         */
        fun setBorderColor(colors: ColorStateList?) {
            if (colors == null) {
                mBorderWidth = 0f
                borderColors = ColorStateList.valueOf(Color.TRANSPARENT)
                mBorderPaint.color = Color.TRANSPARENT
            } else {
                borderColors = colors
                mBorderPaint.color = borderColors.getColorForState(state,
                        DEFAULT_BORDER_COLOR)
            }
        }

        companion object {

            private val TAG = "SelectableRoundedCornerDrawable"
            private val DEFAULT_BORDER_COLOR = Color.BLACK

            fun fromBitmap(bitmap: Bitmap?, r: Resources): SelectableRoundedCornerDrawable? {
                return if (bitmap != null) {
                    SelectableRoundedCornerDrawable(bitmap, r)
                } else {
                    null
                }
            }

            fun fromDrawable(drawable: Drawable?, r: Resources): Drawable? {
                if (drawable != null) {
                    if (drawable is SelectableRoundedCornerDrawable) {
                        return drawable
                    } else if (drawable is LayerDrawable) {
                        val ld = drawable as LayerDrawable?
                        val num = ld!!.numberOfLayers
                        for (i in 0 until num) {
                            val d = ld.getDrawable(i)
                            ld.setDrawableByLayerId(ld.getId(i), fromDrawable(d, r))
                        }
                        return ld
                    }

                    val bm = drawableToBitmap(drawable)
                    if (bm != null) {
                        return SelectableRoundedCornerDrawable(bm, r)
                    } else {
                        Log.w(TAG, "Failed to create bitmap from drawable!")
                    }
                }
                return drawable
            }

            fun drawableToBitmap(drawable: Drawable?): Bitmap? {
                if (drawable == null) {
                    return null
                }

                if (drawable is BitmapDrawable) {
                    return drawable.bitmap
                }

                var bitmap: Bitmap?
                val width = Math.max(drawable.intrinsicWidth, 2)
                val height = Math.max(drawable.intrinsicHeight, 2)
                try {
                    bitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888)
                    val canvas = Canvas(bitmap!!)
                    drawable.setBounds(0, 0, canvas.width, canvas.height)
                    drawable.draw(canvas)
                } catch (e: IllegalArgumentException) {
                    e.printStackTrace()
                    bitmap = null
                }

                return bitmap
            }
        }
    }

    companion object {
        const val TAG = "View"
        const val RECYCLER_VIEW_TAG = "RECYCLER_VIEW_TAG"
        private val sScaleTypeArray = arrayOf(ImageView.ScaleType.MATRIX, ImageView.ScaleType.FIT_XY, ImageView.ScaleType.FIT_START, ImageView.ScaleType.FIT_CENTER, ImageView.ScaleType.FIT_END, ImageView.ScaleType.CENTER, ImageView.ScaleType.CENTER_CROP, ImageView.ScaleType.CENTER_INSIDE)
        private const val DEFAULT_BORDER_COLOR = Color.BLACK
    }

}