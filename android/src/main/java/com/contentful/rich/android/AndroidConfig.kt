package com.contentful.rich.android

import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.FontRes

data class AndroidConfig(
    @ColorRes val textColor: Int = android.R.color.black,
    @ColorInt val linkColor: Int? = null,
    @FontRes val font: Int? = null,
    val textSizeMultiplier: Float = 0.75f,
    val marginTop: Int = 0
)
