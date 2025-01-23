package com.rncamerakit.barcode

import android.content.Context
import android.graphics.*
import android.view.View
import androidx.annotation.ColorInt

import com.rncamerakit.R
import kotlin.math.max
import kotlin.math.min
class BarcodeFrame(context: Context) : View(context) {
    private var borderPaint: Paint = Paint()
    private var laserPaint: Paint = Paint()
    private var overlayPaint: Paint = Paint()
    var frameRect: Rect = Rect()

    private var frameWidth = 0
    private var frameHeight = 0
    private var borderMargin = 0
    private var previousFrameTime = System.currentTimeMillis()
    private var laserY = 0

    private fun init(context: Context) {
        borderPaint.style = Paint.Style.STROKE
        borderPaint.strokeWidth = BORDER_STROKE_WIDTH.toFloat()
        laserPaint.style = Paint.Style.STROKE
        laserPaint.strokeWidth = LASER_STROKE_WIDTH.toFloat()
        // Initialize overlay paint
        overlayPaint.color = Color.parseColor("#80000000")
        overlayPaint.style = Paint.Style.FILL
        borderMargin = context.resources.getDimensionPixelSize(R.dimen.border_length)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val marginHeight = 40
        val marginWidth = 40
        val frameMaxWidth = 700
        val frameMaxHeight = 700
        val frameMinWidth = 100
        val frameMinHeight = 100
        frameWidth = max(frameMinWidth, min(frameMaxWidth, measuredWidth - (marginWidth * 2)))
        frameHeight = max(frameMinHeight, min(frameMaxHeight, measuredHeight - (marginHeight * 2)))
        frameRect.left = (measuredWidth / 2) - (frameWidth / 2)
        frameRect.right = (measuredWidth / 2) + (frameWidth / 2)
        frameRect.top = (measuredHeight / 2) - (frameHeight / 2)
        frameRect.bottom = (measuredHeight / 2) + (frameHeight / 2)
    }

    override fun onDraw(canvas: Canvas) {
        val timeElapsed = System.currentTimeMillis() - previousFrameTime
        super.onDraw(canvas)
        // order determines which one gets painted first
        // laser should be behind borders
        drawOverlay(canvas)
        drawLaser(canvas, timeElapsed)
        drawBorder(canvas)
        previousFrameTime = System.currentTimeMillis()
        this.invalidate(frameRect)
    }

    private fun drawOverlay(canvas: Canvas) {
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), overlayPaint)
        val clearPaint = Paint()
        clearPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        canvas.drawRect(frameRect, clearPaint)
    }

    private fun drawBorder(canvas: Canvas) {
        borderPaint.strokeCap = Paint.Cap.ROUND
        canvas.drawLine(frameRect.left.toFloat(), frameRect.top.toFloat(), frameRect.left.toFloat(), (frameRect.top + borderMargin).toFloat(), borderPaint)
        canvas.drawLine(frameRect.left.toFloat(), frameRect.top.toFloat(), (frameRect.left + borderMargin).toFloat(), frameRect.top.toFloat(), borderPaint)
        canvas.drawLine(frameRect.left.toFloat(), frameRect.bottom.toFloat(), frameRect.left.toFloat(), (frameRect.bottom - borderMargin).toFloat(), borderPaint)
        canvas.drawLine(frameRect.left.toFloat(), frameRect.bottom.toFloat(), (frameRect.left + borderMargin).toFloat(), frameRect.bottom.toFloat(), borderPaint)
        canvas.drawLine(frameRect.right.toFloat(), frameRect.top.toFloat(), (frameRect.right - borderMargin).toFloat(), frameRect.top.toFloat(), borderPaint)
        canvas.drawLine(frameRect.right.toFloat(), frameRect.top.toFloat(), frameRect.right.toFloat(), (frameRect.top + borderMargin).toFloat(), borderPaint)
        canvas.drawLine(frameRect.right.toFloat(), frameRect.bottom.toFloat(), frameRect.right.toFloat(), (frameRect.bottom - borderMargin).toFloat(), borderPaint)
        canvas.drawLine(frameRect.right.toFloat(), frameRect.bottom.toFloat(), (frameRect.right - borderMargin).toFloat(), frameRect.bottom.toFloat(), borderPaint)
    }

    private fun drawLaser(canvas: Canvas, timeElapsed: Long) {
        if (laserY > frameRect.bottom || laserY < frameRect.top) laserY = frameRect.top
        canvas.drawLine((frameRect.left + LASER_STROKE_WIDTH).toFloat(), laserY.toFloat(), (frameRect.right - LASER_STROKE_WIDTH).toFloat(), laserY.toFloat(), laserPaint)
        laserY += (timeElapsed / ANIMATION_SPEED).toInt()
    }

    fun setFrameColor(@ColorInt borderColor: Int) {
        borderPaint.color = borderColor
    }

    fun setLaserColor(@ColorInt laserColor: Int) {
        laserPaint.color = laserColor
    }

    companion object {
        private const val LASER_STROKE_WIDTH = 5
        private const val BORDER_STROKE_WIDTH = 25
        private const val ANIMATION_SPEED = 4
    }

    init {
        init(context)
    }
}
