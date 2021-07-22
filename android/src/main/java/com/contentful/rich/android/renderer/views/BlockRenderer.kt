package com.contentful.rich.android.renderer.views

import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.contentful.java.cda.rich.CDARichBlock
import com.contentful.java.cda.rich.CDARichHyperLink
import com.contentful.java.cda.rich.CDARichNode
import com.contentful.rich.android.AndroidContext
import com.contentful.rich.android.AndroidProcessor
import com.contentful.rich.android.AndroidRenderer
import com.contentful.rich.android.R
import com.contentful.rich.android.renderer.views.span.UrlSpan

open class BlockRenderer(
    processor: AndroidProcessor<View>
) : AndroidRenderer<AndroidContext, View>(processor) {

    override fun canRender(context: AndroidContext?, node: CDARichNode): Boolean = node is CDARichBlock

    override fun render(context: AndroidContext, node: CDARichNode): View? {
        val block = node as CDARichBlock
        val result = inflateRichLayout(context, node)
        val content = result.findViewById<ViewGroup>(R.id.rich_content)
        var lastTextView: TextView? = null
        block.content.forEach { childNode ->
            val childView = processor.process(context, childNode!!)
            if (childView != null) {
                when {
                    childView is TextView -> {
                        lastTextView?.let {
                            it.text = SpannableStringBuilder(it.text).append(" ").append(childView.text)
                        } ?: run {
                            lastTextView = childView
                            content.addView(childView)
                        }
                    }
                    childNode is CDARichHyperLink -> {
                        val childLayout = childView.findViewById<ViewGroup>(R.id.rich_content)
                        if (childLayout.childCount > 0) {
                            val childTextView = childLayout.getChildAt(0) as TextView
                            val span = UrlSpan(childNode.data as String)
                            val text = lastTextView?.let {
                                SpannableStringBuilder(it.text).append(" ").append(childTextView.text)
                            } ?: SpannableStringBuilder(childTextView.text)

                            context.config?.linkColor?.let {
                                span.textColor = it
                            }

                            text.setSpan(
                                span,
                                lastTextView?.text?.length?.plus(1) ?: 0,
                                text.length,
                                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                            )

                            lastTextView?.let {
                                it.movementMethod = LinkMovementMethod.getInstance()
                                it.text = text
                            } ?: run {
                                childTextView.text = text
                                lastTextView = childTextView
                                content.addView(childView)
                            }
                        }
                    }
                    context.path?.size ?: 0 > 1 -> {
                        val indented = context.inflater.inflate(R.layout.rich_indention_layout, null, false)
                        (indented.findViewById<View>(R.id.rich_content) as ViewGroup).addView(childView)
                        content.addView(indented)
                    }
                    else -> content.addView(childView)
                }
            }
        }
        return result
    }

    protected open fun inflateRichLayout(
        context: AndroidContext,
        node: CDARichNode
    ): View = context.inflater.inflate(R.layout.rich_block_layout, null, false)
}
