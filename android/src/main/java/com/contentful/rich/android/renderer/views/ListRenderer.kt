package com.contentful.rich.android.renderer.views

import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.contentful.java.cda.rich.CDARichBlock
import com.contentful.java.cda.rich.CDARichList
import com.contentful.java.cda.rich.CDARichListItem
import com.contentful.java.cda.rich.CDARichNode
import com.contentful.rich.android.AndroidContext
import com.contentful.rich.android.AndroidProcessor
import com.contentful.rich.android.R
import com.contentful.rich.android.renderer.listdecorator.Decorator
import java.util.*
import kotlin.math.roundToInt

class ListRenderer(
    processor: AndroidProcessor<View>,
    vararg decorators: Decorator
) : BlockRenderer(processor) {

    private val decoratorBySymbolMap: MutableMap<CharSequence, Decorator> = HashMap()
    private val decorators: MutableList<Decorator> = ArrayList()

    init {
        this.decorators.addAll(listOf(*decorators))
        for (decorator in decorators) {
            decoratorBySymbolMap[decorator.symbol.toString()] = decorator
        }
    }

    override fun canRender(
        context: AndroidContext?,
        node: CDARichNode
    ): Boolean = if (node is CDARichListItem) {
        context?.topListOfPath?.let {
            decoratorBySymbolMap.containsKey(it.decoration.toString())
        } ?: false
    } else {
        false
    }

    override fun render(
        context: AndroidContext,
        node: CDARichNode
    ): View {
        val block = node as CDARichBlock
        val result = context.inflater.inflate(R.layout.rich_list_layout, null, false) as ViewGroup
        provideDecoration(context, result, node)
        val content = result.findViewById<ViewGroup>(R.id.rich_content)
        block.parseBlock(context, content)
        return result
    }

    private fun provideDecoration(
        context: AndroidContext,
        group: ViewGroup,
        node: CDARichNode?
    ) {
        val decoration = group.findViewById<TextView>(R.id.rich_list_decoration)
        var list = context.topListOfPath
        val childIndex: Int
        val currentDecorator: Decorator? = if (list == null) {
            list = node as CDARichList?
            childIndex = 0
            decoratorBySymbolMap[list?.decoration]
        } else {
            val listIndex = context.path?.indexOf(list) ?: 0
            val listItemIndexOnPath = listIndex + 1
            childIndex = list.content.indexOf(context.path?.get(listItemIndexOnPath) ?: 0)
            val nestedListCount = getListOfTypeCount(context, list).toInt() % Int.MAX_VALUE
            val initialDecorator = decoratorBySymbolMap[list.decoration.toString()]
            val initialDecoratorIndex = decorators.indexOf(initialDecorator)
            val currentPosition = (initialDecoratorIndex + nestedListCount) % decorators.size
            decorators[currentPosition]
        }
        context.config?.also {
            val color = ContextCompat.getColor(context.androidContext, it.textColor)
            decoration.setTextColor(color)
            if (it.marginTop > 0) {
                val layoutParams = RelativeLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                val px = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    it.marginTop.toFloat(),
                    context.androidContext.resources.displayMetrics
                )
                layoutParams.setMargins(0, px.roundToInt(), 0, 0)
                decoration.layoutParams = layoutParams
            }
        }
        decoration.text = currentDecorator?.decorate(childIndex + 1)
    }

    /**
     * Count lists on the path.
     *
     * @param context where is the path stored in? The context!
     * @param list    the list to be listed.
     * @return the number of lists of the supported type.
     */
    private fun getListOfTypeCount(context: AndroidContext, list: CDARichList): Long {
        var count = 0
        context.path?.forEach { node ->
            if (node is CDARichList && node.decoration == list.decoration) {
                count++
            }
        }
        return count.toLong()
    }
}
