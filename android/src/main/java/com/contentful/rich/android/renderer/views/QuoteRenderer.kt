package com.contentful.rich.android.renderer.views

import android.view.View
import com.contentful.java.cda.rich.CDARichNode
import com.contentful.java.cda.rich.CDARichQuote
import com.contentful.rich.android.AndroidContext
import com.contentful.rich.android.AndroidProcessor
import com.contentful.rich.android.R

class QuoteRenderer(
    processor: AndroidProcessor<View>
) : BlockRenderer(processor) {

    override fun canRender(context: AndroidContext?, node: CDARichNode): Boolean = node is CDARichQuote

    override fun inflateRichLayout(
        context: AndroidContext,
        node: CDARichNode
    ): View = context.inflater.inflate(R.layout.rich_quote_layout, null, false)
}
