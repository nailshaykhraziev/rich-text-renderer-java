package com.contentful.rich.android.renderer.views

import android.view.View
import androidx.core.content.ContextCompat
import com.contentful.java.cda.rich.CDARichHorizontalRule
import com.contentful.java.cda.rich.CDARichNode
import com.contentful.rich.android.AndroidContext
import com.contentful.rich.android.AndroidProcessor
import com.contentful.rich.android.AndroidRenderer
import com.contentful.rich.android.R

/**
 * This renderer will render a rich text node into a TextView, respecting it's marks.
 */
class HorizontalRuleRenderer(
    processor: AndroidProcessor<View>
) : AndroidRenderer<AndroidContext, View>(processor) {

    /**
     * Is the incoming node a horizontal rule?
     *
     * @param context context this check should be performed in
     * @param node    node to be checked
     * @return true if the node is a rich rule node.
     */
    override fun canRender(context: AndroidContext?, node: CDARichNode): Boolean = node is CDARichHorizontalRule

    /**
     * Creates a horizontal line.
     *
     * @param context the generic context this node should be rendered in.
     * @param node    the node to be rendered.
     * @return a view representing a horizontal line.
     */
    override fun render(
        context: AndroidContext,
        node: CDARichNode
    ): View = context.inflater.inflate(R.layout.rich_horizontal_rule_layout, null).apply {
        context.config?.textColor?.let {
            findViewById<View>(R.id.divider).setBackgroundColor(context.androidContext.getColor(it))
        }
    }
}
