package com.example.myfirstapp.extentions

import android.graphics.LinearGradient
import android.graphics.Rect
import android.graphics.Shader
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import kotlin.math.min

//textview
class ClickSpan(val listener: View.OnClickListener?) : ClickableSpan() {

    override fun onClick(widget: View) {
        listener?.onClick(widget)
    }
}
fun TextView.clickify(clickableText: String, listener: View.OnClickListener) {
    val text = text
    val string = text.toString()
    val span = ClickSpan(listener)

    val start = string.indexOf(clickableText)
    val end = start + clickableText.length
    if (start == -1) return

    if (text is Spannable) {
        text.setSpan(span, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    } else {
        val s = SpannableString.valueOf(text)
        s.setSpan(span, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        setText(s)
    }

    val m = movementMethod
    if (m == null || m !is LinkMovementMethod) {
        movementMethod = LinkMovementMethod.getInstance()
    }
}
fun TextView.addFadeOutToText(galleryHeight: Int, @ColorRes fromColor: Int, @ColorRes toColor: Int) {

    val bounds = Rect()
    val textPaint = paint
    val text = text
    textPaint.getTextBounds(text.toString(), 0, text.length, bounds)
    val viewWidth = width
    val startHeight = if (lineCount > 2) galleryHeight / 2 else 0
    val availableWidth =
        (viewWidth - paddingLeft - paddingRight - textPaint.measureText(text.toString())).toInt()
    if (availableWidth < 0) {
        val textShader = LinearGradient(
            (3 * viewWidth / 4).toFloat(), startHeight.toFloat(), viewWidth.toFloat(), paint.textSize,
            intArrayOf(ContextCompat.getColor(context, fromColor), ContextCompat.getColor(context, toColor)), null, Shader.TileMode.CLAMP
        )
        paint.shader = textShader
    }
}


//
fun insertPeriodically(text: String, insert: String, period: Int): String {
    val builder = StringBuilder(
        text.length + insert.length * (text.length / period) + 1
    )
    var index = 0
    var prefix = ""
    while (index < text.length) {
        builder.append(prefix)
        prefix = insert
        builder.append(text.substring(index, min(index + period, text.length)))
        index += period
    }
    return builder.toString()
}