package com.contentful.rich.android.renderer.views.span

import android.text.TextPaint
import android.text.style.URLSpan
import androidx.annotation.ColorInt

class UrlSpan(url: String) : URLSpan(url) {

    @ColorInt
    var textColor: Int? = null

    override fun updateDrawState(ds: TextPaint) {
        textColor?.also {
            ds.linkColor = it
        }
        super.updateDrawState(ds)
    }
}
