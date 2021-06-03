package com.zjgsu.cxxu.TGraph.customizedViews

import Tree
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.util.Log
import android.view.*
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.ScaleGestureDetector.OnScaleGestureListener
import com.nineoldandroids.animation.ObjectAnimator
import com.nineoldandroids.view.ViewHelper
import com.zjgsu.cxxu.TGraph.Lg
import com.zjgsu.cxxu.TGraph.Lg.d
import com.zjgsu.cxxu.TGraph.customizedViews.treeLayoutManager.BaseTreeLayoutManager
import com.zjgsu.cxxu.TGraph.customizedViews.viewInterfaces.TreeViewItemLongClick
import com.zjgsu.cxxu.TGraph.mvp.model.Node
import com.zjgsu.cxxu.TGraph.mvp.model.line.CubicInterpolator
import com.zjgsu.cxxu.TGraph.util.DensityUtils.dp2px
import com.zjgsu.cxxu.TGraph.util.DragFlagSupporter
import com.zjgsu.cxxu.TGraph.util.DragFlagSupporter.LooperListener
import com.zjgsu.cxxu.TGraph.util.StringTool.showToast
import com.zjgsu.cxxu.TGraph.util.control.MoveAndScaleHandler
import java.util.*
import kotlin.math.abs

/*自定义控件:*/
/**
 * test
 *
 *
 * 本控件实现手势缩放监听器接口
 * 缩放/比例/刻度(scale)
 * android.view public class ScaleGestureDetector
 * extends Object
 * Detects scaling transformation gestures using the supplied MotionEvents.
 * The ScaleGestureDetector.OnScaleGestureListener callback will notify users when a particular gesture event has occurred.
 * This class should only be used with MotionEvents reported via touch.
 * To use this class:
 * Create an instance of the ScaleGestureDetector for your ItemView
 * In the ItemView.onTouchEvent(MotionEvent) method :
 *
 *  * ensure you call onTouchEvent(MotionEvent).
 *  * The methods defined in your callback will be executed when the events occur.
 *
 *
 */
/**
 * 本view将实现为微件容器(ViewGroup)
 * 同时,本控件实现手势缩放监听器接口
 * 缩放/比例/刻度(scale)
 * android.view public class ScaleGestureDetector
 * extends Object
 * Detects scaling transformation gestures using the supplied MotionEvents.
 * The ScaleGestureDetector.OnScaleGestureListener callback will notify users when a particular gesture event has occurred.
 * This class should only be used with MotionEvents reported via touch.
 * To use this class:
 * Create an instance of the ScaleGestureDetector for your ItemView
 *
 *  * In the ItemView.onTouchEvent(MotionEvent) method :
 *  * ensure you call onTouchEvent(MotionEvent).
 * The methods defined in your callback will be executed when the events occur.
 *
 *
 */
class TreeView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    ViewGroup(context, attrs, defStyleAttr), OnScaleGestureListener {
    companion object {
        private const val TAG = "TreeView"
    }

    //树形结构
    lateinit var mTree: Tree<String>

    /*Context:interface to global information about an application environment. */
    private val contextT: Context
    private var mTreeLayoutManager: BaseTreeLayoutManager? = null

    //移动控制
    private val mMoveAndScaleHandler: MoveAndScaleHandler

    //长按
    private var mTreeViewItemLongClick: TreeViewItemLongClick? = null

    //最近点击的结点
    var mCurrentFocusNode: Node<String>? = null
        private set
    private var mWidth = 0
    private var mHeight = 0

    //触摸循环事件，放大，等同，缩小
    private val mLooperBody = arrayOf(0, 1, 0, -1)
    private val mLooperFlag: DragFlagSupporter<Int>
    private val mGestureDetector: GestureDetector

    /**android.graphics
     * public class Paint
    extends Object
    The Paint class holds the style and color information about how to draw geometries, text and bitmaps.*/
    private val mPaint: Paint

    /**android.graphics
     * public class Path
    extends Object
    The Path class encapsulates compound合成；加重；构成 (multiple contour轮廓；外形) geometric paths consisting of straight line segments, quadratic curves, and cubic curves.
    It can be drawn with canvas.drawPath(path, paint), either filled or stroked (based on the paint's Style), or it can be used for clipping or to draw text on a path.*/
    private val mPath: Path

    /**
     * onMeasure(int, int)
     * Overrides:
     * onMeasure in class ItemView
     *
     *
     * 调用以确定此视图及其所有子级的大小要求。
     * 这时一个没有返回值的方法
     * 或许您可以参考TextView中onMeasure方法的重写
     * 本app中,树形图(结点)每发生变化,就会引发一次测量(包括结点的增加/删除/内容的修改!)
     */
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        /*Returns the number of children in the group*/
        val size = childCount
        for (i in 0 until size) {
            /**
             * protected void measureChild(android.view.ItemView child,
             * int parentWidthMeasureSpec,
             * int parentHeightMeasureSpec)
             * Ask one of the children of this view to measure itself, taking into account both the MeasureSpec requirements for this view and its padding. The heavy lifting is done in getChildMeasureSpec. */
            measureChild(getChildAt(i), widthMeasureSpec, heightMeasureSpec)
        }
        /**
         * public final int getMeasuredWidth()
         * Like getMeasuredWidthAndState(), but only returns the raw width component (that is the result is masked by MEASURED_SIZE_MASK). */
        mWidth = measuredWidth
        mHeight = measuredHeight
        if (mTreeLayoutManager != null) {
            //树形结构的分布
            d(TAG, "measuring the size of the view and its descendents..")
            mTreeLayoutManager!!.onTreeLayout(this)
            val viewBoxModel = mTreeLayoutManager!!.onTreeLayoutCallBack()
            /**
             * android.view.ItemView
             * protected final void setMeasuredDimension(int measuredWidth,
             * int measuredHeight)
             * This method must be called by onMeasure(int, int) to store the measured width and measured height. Failing to do so will trigger an exception at measurement time.
             */
            setMeasuredDimension(
                viewBoxModel!!.right + abs(viewBoxModel.left), viewBoxModel.bottom + abs(
                    viewBoxModel.top
                )
            )
            boxCallBackChange()
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val childCount = childCount
        for (i in 0 until childCount) {
            val childAt = getChildAt(i)
            childAt.layout(childAt.left, childAt.top, childAt.right, childAt.bottom)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mWidth = w
        mHeight = h
    }

    fun looperBusiness(item: Int) {
        /**/
        val easeCubicInterpolator = CubicInterpolator(0.39f, 0.13f, 0.33f, 1f)
        /*使用nineoldandroids动画库*/
        val animator1: ObjectAnimator
        val animator2: ObjectAnimator
        when (item) {
            -1 -> {
                /*Constructs and returns an ObjectAnimator that animates between float values. A single value implies说明/意味着 that that value is the one being animated to. Two values imply a starting and ending values. More than two values imply a starting value, values to animate through along the way, and an ending value (these values will be distributed evenly across the duration of the animation).*/
                animator1 = ObjectAnimator.ofFloat(this@TreeView, "scaleX", scaleX, 0.3f)
                    .setDuration(500)
                animator2 = ObjectAnimator.ofFloat(this@TreeView, "scaleY", scaleX, 0.3f)
                    .setDuration(500)
            }
            0 -> {
                animator1 = ObjectAnimator.ofFloat(this@TreeView, "scaleX", scaleX, 1.0f)
                    .setDuration(500)
                animator2 = ObjectAnimator.ofFloat(this@TreeView, "scaleY", scaleX, 1.0f)
                    .setDuration(500)
            }
            else -> {
                animator1 = ObjectAnimator.ofFloat(this@TreeView, "scaleX", scaleX, 1.6f)
                    .setDuration(500)
                animator2 = ObjectAnimator.ofFloat(this@TreeView, "scaleY", scaleX, 1.6f)
                    .setDuration(500)
            }
        }
        animator1.interpolator = easeCubicInterpolator
        animator2.interpolator = easeCubicInterpolator
        Lg.d(TAG, "start the animation in the looperBusiness()")
        animator1.start()
        animator2.start()
    }

    private fun boxCallBackChange() {
        val dy = dp2px(context.applicationContext, 20f)
        val moreWidth = dp2px(context.applicationContext, 200f)
        val treeViewSize = mTreeLayoutManager!!.onTreeLayoutCallBack()
        Lg.i(TAG, "size=" + treeViewSize.toString())
        val w = treeViewSize!!.right + dy
        val h = treeViewSize.bottom + abs(treeViewSize.top)
        Lg.i(TAG, "beLayout: $measuredWidth,$measuredHeight")

        //重置View的大小
        Lg.d(TAG, "resetting the size of the tree view")
        /*拿到当前树形控件的layoutParams:
        * Get the LayoutParams associated with this view. All views should have layout parameters. These supply parameters to the parent of this view specifying how it should be arranged. */
        val layoutParams = this.layoutParams
        layoutParams.height = if (h > measuredHeight) h + moreWidth else measuredHeight
        layoutParams.width = if (w > measuredWidth) w + moreWidth else measuredWidth
        Lg.i(TAG, "onLayout: $w,$h")

        //移动节点
        val rootNode = tree.getRootNode()
        if (rootNode != null) {
            Lg.d(TAG, "moving the nodes.")
            moveNodeLayout(this, findNodeViewFromNodeModels(rootNode) as NodeView?, abs(treeViewSize.top))
        }
    }

    /**
     * 移动
     *
     * @param rootView
     * @param dy
     */
    private fun moveNodeLayout(superTreeView: TreeView, rootView: NodeView?, dy: Int) {
        var lRootView = rootView
        if (dy == 0) {
            return
        }
        val queue: Deque<Node<String>?> = ArrayDeque()
        var rootNode = lRootView!!.treeNode
        /*广度遍历树结点*/
        queue.add(rootNode)
        while (!queue.isEmpty()) {
            rootNode = queue.poll()
            lRootView = superTreeView.findNodeViewFromNodeModels(rootNode) as NodeView?
            val l = lRootView!!.left
            val t = lRootView.top + dy
            lRootView.layout(l, t, l + lRootView.measuredWidth, t + lRootView.measuredHeight)
            val childNodes = rootNode!!.childNodes
            for (item in childNodes) {
                queue.add(item)
            }
        }
    }

    override fun dispatchDraw(canvas: Canvas) {
        /**android.view.ViewGroup
         * protected void dispatchDraw(android.graphics.Canvas canvas)
        Called by draw to draw the child views.
        canvas – the canvas on which to draw the view
        This may be overridden by derived classes to gain control just before its children are drawn (but after its own view has been drawn).*/
        super.dispatchDraw(canvas)
        drawTreeLine(canvas, mTree.getRootNode())
    }

    /**
     * 绘制树形的连线
     *通过循环调用drawLineToView(),递归调用drawTreeLine
     * @param canvas
     * @param startNode
     */
    private fun drawTreeLine(canvas: Canvas, startNode: Node<String>) {
        /**The Canvas class holds the "draw" calls.
         * To draw something, you need 4 basic components: A Bitmap to hold the pixels, a Canvas to host the draw calls (writing into the bitmap), a drawing primitive图元 (e.g. Rect, Path, text, Bitmap), and a paint (to describe the colors and styles for the drawing).*/
        Lg.d(TAG, "drawing the line(curve)..from${startNode.value} node")
        val parentView = findNodeViewFromNodeModels(startNode) as NodeView?
        if (parentView != null) {
            val childNodes = startNode.childNodes
            for (node in childNodes) {

                //连线
                drawLineToView(canvas, parentView, findNodeViewFromNodeModels(node))

                //递归
                drawTreeLine(canvas, node)
            }
        }
    }

    /**
     * 绘制两个View(NodeView结点控件)之间的连线
     *
     * @param canvas
     * @param start
     * @param end
     */
    private fun drawLineToView(canvas: Canvas, start: View, end: View?) {
        if (end!!.visibility == GONE) {
            return
        }
        /**
        android.graphics.Paint.Style
        public static final Paint.Style STROKE
        Enum constant ordinal: 1
         * Geometry and text drawn with this style will be stroked, respecting the stroke-related fields on the paint.
         * 将按照此样式绘制的几何图形和文本进行描边，并遵守绘画上与描边相关的字段*/
        val top = start.top
        val formY = top + start.measuredHeight / 2
        val formX = start.right
        val top1 = end.top
        val toY = top1 + end.measuredHeight / 2
        val toX = end.left
        val width = 2f
        mPaint.style = Paint.Style.STROKE
        mPaint.strokeWidth = dp2px(contextT, width).toFloat()
        /**Clear any lines and curves from the path, making it empty. This does NOT change the fill-type setting.*/
        mPath.reset()
        /**Set the beginning of the next contour to the point (x,y).*/
        Lg.d(TAG,"Set the beginning of the next contour to the point (x,y)")
        mPath.moveTo(formX.toFloat(), formY.toFloat())
        /**android.graphics.Canvas
         * Add a quadratic二次曲线 bezier from the last point, approaching control point (x1,y1), and ending at (x2,y2). If no moveTo() call has been made for this contour, the first point is automatically set to (0,0).*/
        mPath.quadTo((toX - dp2px(contextT, 15f)).toFloat(), toY.toFloat(), toX.toFloat(), toY.toFloat())
        canvas.drawPath(mPath, mPaint)
    }

    /**
     * 本方法还有一个重载(而不同于重写)
     * ItemView Implement this method to handle touch screen motion events.
     * If this method is used to detect click actions, it is recommended that the actions be performed by implementing and calling performClick(). This will ensure consistent system behavior, including:
     * obeying click sound preferences
     * dispatching OnClickListener calls
     * handling ACTION_CLICK when accessibility features are enabled
     * Overrides:
     * onTouchEvent in class ItemView
     * Params:
     * event – The motion event.
     * Returns:
     * True if the event was handled, false otherwise.
     *
     * @param event
     * @return
     */
    override fun onTouchEvent(event: MotionEvent): Boolean {
        mGestureDetector.onTouchEvent(event)
        d(TAG, "test the GestureDetector")
        return mMoveAndScaleHandler.onTouchEvent(event)
    }

    var tree: Tree<String>
        get() = mTree
        set(Tree) {
            mTree = Tree
            clearAllNoteViews()
            addNoteViews()
            setCurrentSelectedNode(mTree.getRootNode())
        }

    /**
     * 中点对焦
     */
    fun focusMidLocation() {

        //计算屏幕中点
        /**
         * android.content.Context
         * public abstract Object getSystemService(String name)
         * Return the handle(句柄) to a system-level service by name.
         * The class of the returned object varies by the requested name. */
        val systemService = contextT.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val defaultDisplay = systemService.defaultDisplay
        val displayH = defaultDisplay.height
        dp2px(contextT, 20f)
        val focusY = displayH / 2

        //回到原点(0,0)
        ViewHelper.setTranslationX(this, 0f)
        ViewHelper.setTranslationY(this, 0f)
        val view = findNodeViewFromNodeModels(mTree.getRootNode())
        //回到原点后的中点
        var pointY = view!!.y.toInt() + view.measuredHeight / 2
        pointY = if (pointY >= focusY) {
            -(pointY - focusY)
        } else {
            focusY - pointY
        }
        ViewHelper.setTranslationY(this, pointY.toFloat())
    }

    /**
     * 清除所有的NoteView
     */
    private fun clearAllNoteViews() {
        val count = childCount
        if (count > 0) {
            for (i in 0 until count) {
                val childView = getChildAt(i)
                (childView as? NodeView)?.let { removeView(it) }
            }
        }
    }

    /**
     * 添加所有的NoteView
     */
    private fun addNoteViews() {
        val rootNode = mTree.getRootNode()
        val deque: Deque<Node<String>?> = ArrayDeque()
        deque.add(rootNode)
        while (!deque.isEmpty()) {
            val poll = deque.poll()
            addNodeWidgetToTreeViewGroup(poll)
            val childNodes = poll!!.childNodes
            for (ch in childNodes) {
                deque.push(ch)
            }
        }
    }

    private fun addNodeWidgetToTreeViewGroup(node: Node<String>?): View {
        /*简单地创建一个结点微件实例*/
        val nodeView = NodeView(contextT)
        /**
         * android.view.ItemView
         * public void setFocusable(boolean focusable)
         * Set whether this view can receive the focus.
         * Setting this to false will also ensure that this view is not focusable in touch mode.
         * I think,this may be like the camera capture the photos */
        nodeView.isFocusable = true
        nodeView.isClickable = true
        /**
         * Note that selection is not the same as focus.
         * the selected view is the view that is highlighted.
         */
        nodeView.isSelected = false
        nodeView.setNode(node!!)
        /**
         * android.view.ViewGroup.LayoutParams public LayoutParams(int width,
         * int height)
         * Creates a new set of layout parameters with the specified width and height. */
        val layoutParams = LayoutParams(
            LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT
        )
        /**
         * android.view.ItemView
         * public void setLayoutParams(android.view.ViewGroup.LayoutParams params)
         * Set the layout parameters associated with this view.
         * These supply parameters to the parent of this view specifying how it should be arranged.
         * There are many subclasses of ViewGroup.LayoutParams, and these correspond to the different subclasses of ViewGroup that are responsible for arranging their children */
        nodeView.layoutParams = layoutParams
        //set the node_click event(设置结点微件的点击事件)
//        nodeView.setOnClickListener(view -> performTreeItemClick(view));
        nodeView.setOnClickListener { v: View? ->
            d(TAG, "test the click:")
            setCurrentSelectedNode((v as NodeView).treeNode)
            /*使用toast时,不要忘记show()的调用*/
//            Toast.makeText(
//                contextT,
//                "you'd long click the node to edit it",
//                Toast.LENGTH_SHORT
//            ).show()

            "you'd long click the node to edit it".showToast()

//            true
        }
        /**
         * android.view.ItemView
         * public void setOnLongClickListener(ItemView.OnLongClickListener l)
         * Register a callback to be invoked when this view is clicked and held. If this view is not long clickable, it becomes long clickable */
        nodeView.setOnLongClickListener { v: View ->
            preformTreeItemLongClick(v)
            true
        }
        /*通过代码的形式动态地添加一个节点控件到树形布局(微件容器)中
         * 该方法是继承自ViewGroup类*/this.addView(nodeView)
        /*返回被添加地控件引用*/return nodeView
    }

    fun setTreeViewItemClick() {
//        mTreeViewItemClick = treeViewItemClick;
    }

    fun setTreeViewItemLongClick(treeViewItemLongClick: TreeViewItemLongClick?) {
        mTreeViewItemLongClick = treeViewItemLongClick
    }

    private fun preformTreeItemLongClick(v: View) {
        setCurrentSelectedNode((v as NodeView).treeNode)
        if (mTreeViewItemLongClick != null) {
            mTreeViewItemLongClick!!.onLongClick(v)
        }
    }

    fun setCurrentSelectedNode(nodeModel: Node<String>?) {
        if (mCurrentFocusNode != null) {
            mCurrentFocusNode!!.isFocus = false
            val treeNodeView = findNodeViewFromNodeModels(mCurrentFocusNode) as NodeView?
            if (treeNodeView != null) {
                treeNodeView.isSelected = false
            }
        }
        nodeModel!!.isFocus = true
        findNodeViewFromNodeModels(nodeModel)!!.isSelected = true
        mCurrentFocusNode = nodeModel
    }

    /**
     * 设置树形结构分布管理器
     *
     * @param treeLayoutManager
     */
    fun setTreeLayoutManager(treeLayoutManager: BaseTreeLayoutManager?) {
        mTreeLayoutManager = treeLayoutManager
    }

    /**
     * 模型查找NodeView
     * with the method ,you may modify the node in the tree easily
     *
     * @param nodeModel the instance of the nodeModel to find the correspond NodeView instance of the tree
     * @return ItemView instance
     */
    fun findNodeViewFromNodeModels(nodeModel: Node<String>?): View? {
        var view: View? = null

        /**
         * android.view.ViewGroup
         * public int getChildCount()
         * Returns the number of children in the group.
         * the getChildCount will get the size of the widgets in the treeViewGroup  */
        val size = childCount
        for (i in 0 until size) {
            /**
             * android.view.ViewGroup
             * public android.view.ItemView getChildAt(int index)
             * Returns the view at the specified position in the group. */
            val childView = getChildAt(i)
            /*判断并处理树型ViewGroup中的NodeView微件*/if (childView is NodeView) {
                /*向下转型:利用NodeView微件提供的方法,得到微件实例中地成员对象NodeModel实例
                 * 此时的实例就是treeNode*/
                val treeNodeModel = childView.treeNode
                if (treeNodeModel == nodeModel) {
                    view = childView
                    continue
                }
            }
        }
        return view
    }

    /**
     * 改变某个结点的值
     *
     * @param model the Node to be modify
     * @param value the new value (String)
     */
    fun modifyNodeValue(model: Node<String>?, value: String) {
        val treeNodeView = findNodeViewFromNodeModels(model) as NodeView?
        val treeNodeModel = treeNodeView!!.treeNode
        treeNodeModel!!.value = value
        /*set the treeNodeView with the new value treeNodeModel*/treeNodeView.setNode(treeNodeModel)
    }

    /**
     * 添加同层节点
     *
     * @param nodeValue
     */
    fun addBrotherNode(nodeValue: String) {
        val node = Node(nodeValue)
        val parentNode = mCurrentFocusNode!!.parentNode
        if (parentNode != null) {
            mTree.addNode(parentNode, node)
            Log.i(TAG, "addNode: true")
            addNodeWidgetToTreeViewGroup(node)
        }
    }

    /**
     * 添加子节点
     * the method is simple method ,the core operation is to judge which nodeView is focused currently.
     *
     * @param nodeValue
     */
    fun addSubNode(nodeValue: String) {
        val addNode = Node(nodeValue)
        mTree.addNode(mCurrentFocusNode!!, addNode)
        addNodeWidgetToTreeViewGroup(addNode)
    }

    fun deleteNode(node: Node<String>) {

        //设置current的选择
        setCurrentSelectedNode(node.parentNode)
        val parentNode = node.parentNode
        if (parentNode != null) {
            //切断
            mTree.removeNode(parentNode, node)
        }

        //清理碎片
        val queue: Queue<Node<String>> = ArrayDeque()
        queue.add(node)
        while (!queue.isEmpty()) {
            /*thanks to the isEmpty()judge,the polledNode there would never be null*/
            val polledNode = queue.poll()
            val treeNodeView = findNodeViewFromNodeModels(polledNode) as NodeView?
            removeView(treeNodeView)
            for (nm in polledNode!!.childNodes) {
                queue.add(nm)
            }
        }
    }

    override fun onScale(detector: ScaleGestureDetector): Boolean {
        val scaleFactor = detector.scaleFactor
        scaleX = scaleFactor
        scaleY = scaleFactor
        return false
    }

    override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
        return true
    }

    override fun onScaleEnd(detector: ScaleGestureDetector) {}


    /*全参数构造函数*/ /*重载构造函数:在kotlin中,您可以用默认参数尝试简化这种写法*/
    init {
        /**
         * android.view.ViewGroup
         * public void setClipChildren(boolean clipChildren)
         * By default, children are clipped to their bounds before drawing.
         * This allows view groups to override this behavior for animations, etc.
         */
        clipChildren = false
        /**
         * Sets whether this ViewGroup will clip its children to its padding and resize (but not clip) any EdgeEffect to the padded region, if padding is present.
         * By default, children are clipped to the padding of their parent ViewGroup. This clipping behavior is only enabled if padding is non-zero.
         */
        clipToPadding = false
        mPaint = Paint()
        mPaint.isAntiAlias = true
        mPath = Path()
        mPath.reset()
        mMoveAndScaleHandler = MoveAndScaleHandler(this)
        this.contextT = context
        mLooperFlag = DragFlagSupporter(mLooperBody, object : LooperListener<Int> {
            override fun onLooper(item: Int) {
                looperBusiness(item)
            }
        })
        /*当缩放的时候会调用该函数(双击缩放)*/
        mGestureDetector = GestureDetector(this.contextT, object : SimpleOnGestureListener() {
            override fun onDoubleTap(e: MotionEvent): Boolean {
                mLooperFlag.next()
                d(TAG, "test the DragFlagSupporter:you double clicked the screen of the rectangle ?")
                return true
            }
        })
    }
}