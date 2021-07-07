package com.contentful.rich.android.renderer.views

import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.BackgroundColorSpan
import android.text.style.StyleSpan
import android.text.style.TextAppearanceSpan
import android.text.style.UnderlineSpan
import android.util.TypedValue
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.contentful.java.cda.rich.CDARichHeading
import com.contentful.java.cda.rich.CDARichMark.*
import com.contentful.java.cda.rich.CDARichNode
import com.contentful.java.cda.rich.CDARichText
import com.contentful.rich.android.AndroidContext
import com.contentful.rich.android.AndroidProcessor
import com.contentful.rich.android.AndroidRenderer
import com.contentful.rich.android.R
import javax.annotation.Nonnull

/**
 * This renderer will render a rich text node into a TextView, respecting it's marks.
 */
class TextRenderer(
    processor: AndroidProcessor<View>,
) : AndroidRenderer<AndroidContext, View>(processor) {

    /**
     * Is the incoming node a rich text?
     *
     * @param context context this check should be performed in
     * @param node    node to be checked
     * @return true if the node is a rich text node.
     */
    override fun canRender(context: AndroidContext?, @Nonnull node: CDARichNode): Boolean = node is CDARichText

    /**
     * Converts the incoming rich text into a string and adds spans according to its markers.
     *
     * @param context the generic context this node should be rendered in.
     * @param node    the node to be rendered.
     * @return a view containing the text content of the rich text and decorations based on its markers.
     */
    override fun render(@Nonnull context: AndroidContext, @Nonnull node: CDARichNode): View? {
        val richText = node as CDARichText
        val textContent = SpannableStringBuilder(richText.text)
        val result = context.inflater.inflate(R.layout.rich_text_layout, null)
        val content: TextView = result.findViewById(R.id.rich_content)

        context.config?.also { config ->
            content.setTextColor(ContextCompat.getColor(context.androidContext, config.textColor))


            config.font?.also {
                content.typeface = ResourcesCompat.getFont(context.androidContext, it)
            }
        }
        richText.marks.forEach { mark ->
            val span = when (mark) {
                is CDARichMarkUnderline -> UnderlineSpan()
                is CDARichMarkBold -> StyleSpan(Typeface.BOLD)
                is CDARichMarkItalic -> StyleSpan(Typeface.ITALIC)
                is CDARichMarkCode -> TextAppearanceSpan("monospace", 0, 0, null, null)
                else -> BackgroundColorSpan(-0x7f000100)
            }
            textContent.setSpan(span, 0, textContent.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        context.path?.forEach { pathNode ->
            (pathNode as? CDARichHeading)?.let {
                content.setTextSize(
                    TypedValue.COMPLEX_UNIT_PX,
                    content.textSize * (7f - it.level) * (context.config?.textSizeMultiplier ?: 0.75f)
                )
            }
        }
        content.text = textContent

        return result
    }
}
