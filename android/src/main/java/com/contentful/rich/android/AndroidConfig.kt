package com.contentful.rich.android

import androidx.annotation.ColorRes
import androidx.annotation.FontRes

data class AndroidConfig(
    @ColorRes val textColor: Int = android.R.color.black,
    @FontRes val font: Int? = null,
    val textSizeMultiplier: Float = 0.75f
)
