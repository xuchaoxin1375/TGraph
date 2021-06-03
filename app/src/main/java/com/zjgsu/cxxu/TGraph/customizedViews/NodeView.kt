package com.zjgsu.cxxu.TGraph.customizedViews

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.zjgsu.cxxu.TGraph.R
import com.zjgsu.cxxu.TGraph.mvp.model.Node

/**
 *
 */
@SuppressLint("AppCompatCustomView") //Indicates that Lint should ignore the specified warnings for the annotated element.
class NodeView @JvmOverloads constructor(context: Context?, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    TextView(context, attrs, defStyleAttr) {/*public Node<String> node*/
    /**
     * @return Node<String>
    </String> */
    var treeNode: Node<String>? = null
    fun setNode(node: Node<String>) {
        treeNode = node
        /*
          android.widget.TextView
         public void setSelected(boolean selected)

         android.view.ItemView Changes the selection state of this view.
         A view can be selected or not.
        <li></li>Note that selection is not the same as focus.
         Views are typically selected in the context of an AdapterView like ListView or GridView; t
         he selected view is the view that is highlighted.*/isSelected = node.isFocus
        /**android.widget.TextView
         * public final void setText(CharSequence text)
         * Sets the text to be displayed.  */
        text = node.value
    }

    /**
     * android.util public interface AttributeSet
     * A collection of attributes, as found associated with a tag in an XML document.
     * Often you will not want to use this interface directly, instead passing it to Resources.
     */
    init {
        setTextColor(Color.WHITE)
        setPadding(12, 10, 12, 10)
        /**
         * You should use the following code from the support library instead:
         *
         * ContextCompat.getDrawable(context, R.drawable.***)
         * Using this method is equivalent to calling:
         *
         * if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
         * return resources.getDrawable(id, context.getTheme());
         * } else {
         * return resources.getDrawable(id);
         * }
         * As of API 21, you should use the getDrawable(int, Theme) method
         * instead of getDrawable(int),
         * as it allows you to fetch a drawable object associated with a particular resource ID for the given screen density/theme. Calling the deprecated getDrawable(int) method is equivalent to calling getDrawable(int, null).
         */
        val drawable = ContextCompat.getDrawable(context!!, R.drawable.node_view_bg)
        background = drawable
    }
}