package com.zjgsu.cxxu.TGraph.customizedViews.treeLayoutManager

import Tree
import android.util.Log
import com.zjgsu.cxxu.TGraph.Lg.d
import com.zjgsu.cxxu.TGraph.customizedViews.NodeView
import com.zjgsu.cxxu.TGraph.customizedViews.TreeView
import com.zjgsu.cxxu.TGraph.mvp.model.Node
import com.zjgsu.cxxu.TGraph.mvp.model.TreeItem
import com.zjgsu.cxxu.TGraph.mvp.model.TreeViewSize
import java.util.*

/*make TAG in the outer the class is not very good for IDEA/AS to display the source file (especially when refactoring)*/
//private const val TAG = "TreeLayoutManager"
class TreeLayoutManager(dx: Int, dy: Int, height: Int) : BaseTreeLayoutManager {
    companion object {
        private const val TAG = "TreeLayoutManager"
    }

    /*operation codes:*/
    private val msg_standard_layout = 1
    private val msg_correct_layout = 2
    private val msg_box_call_back = 3
    private val mTreeViewSize: TreeViewSize?
    private val mDy: Int
    private val mDx: Int
    private val mHeight: Int

    /**
     * implement the methods defined in the BaseTreeLayoutManager :*/
    /*本函数将辅助onMeasure()的重写实现,在树形控件发生变化时,执行计算*/
    override fun onTreeLayout(treeView: TreeView?) {
        d(TAG, "calculating and setting the treeLayout...")
        val tree = treeView!!.mTree
        val rootNodeView = treeView.findNodeViewFromNodeModels(tree.getRootNode())
        if (rootNodeView != null) {
            //根节点位置
            setRootNodeViewLayout(rootNodeView as NodeView)
        }
        d(TAG, "there")
        with(tree) {
            if (rootNodeView != null) {
                //根节点位置
                setRootNodeViewLayout(rootNodeView as NodeView)
            }
            d(TAG, "adding a Item to the tree")
            /*there is in the scope function with()
            * remember the instance as the parameter of with()*/
            /**
            public final fun addTreeItem(TreeItem: TreeItem<Node<T>>?): Unit
            set the member mTreeItem with the param TreeItem:
            object:InterfaceNameToBeImplemented{}*/
            /*setTreeItem的调用者时Tree类型的对象(引用变量)*/
            setTreeItem(object : TreeItem<Node<String>> {
                override fun setNext(msgId: Int, nextNode: Node<String>) {
                    d(TAG, "msgId:$msgId,nextNode:value:${nextNode.value}")
                    doNextNode(msgId, nextNode, treeView)
                }
            })
            /*test optimal:*/
//            addTreeItem{doNext(treeView)}

            //基本布局
            traverseTreeInWidth(msg_standard_layout)

            //纠正
            traverseTreeInWidth(msg_correct_layout)
        }
        mTreeViewSize!!.clear()
        d(TAG, "the treeSize was cleared")
        tree.traverseTreeInDeep(msg_box_call_back)
    }

    override fun onTreeLayoutCallBack(): TreeViewSize? {
        d(TAG, "test onTreeLayoutCallBack:return the mTreeViewSize of the TreeLayoutManager.")
        return mTreeViewSize
    }

    /**
     * 布局纠正
     *
     * @param treeView
     * @param next
     */
    override fun correctTreeLayout(treeView: TreeView, next: NodeView?) {
        d(TAG, "correcting the TreeLayout")
        val mTree: Tree<String> = treeView.mTree
        val count = next!!.treeNode!!.childNodes.size
        if (next.parent != null && count >= 2) {
            val topNode = next.treeNode!!.childNodes[0]
            val bottomNode = next.treeNode!!.childNodes[count - 1]
            Log.i(TAG, next.treeNode!!.value + ":" + topNode.value + "," + bottomNode.value)
            val topDr = next.top - treeView.findNodeViewFromNodeModels(topNode)!!.bottom + mDy
            val bottomDr = treeView.findNodeViewFromNodeModels(bottomNode)!!.top - next.bottom + mDy

            //上移动
            val allLowNodes: ArrayList<Node<String>> = mTree.getAllLowNodes(bottomNode)
            val allPreNodes: ArrayList<Node<String>> = mTree.getAllPreNodes(topNode)
            for (low in allLowNodes) {
                val view = treeView.findNodeViewFromNodeModels(low) as NodeView
                moveNodeLayout(treeView, view, bottomDr)
            }
            for (pre in allPreNodes) {
                val view = treeView.findNodeViewFromNodeModels(pre) as NodeView
                moveNodeLayout(treeView, view, -topDr)
            }
        }
    }

    private fun doNextNode(msgId: Int, nextNode: Node<String>, treeView: TreeView) {
        d(TAG, "doNext..")
        val nodeView = treeView.findNodeViewFromNodeModels(nextNode)
        if (msgId == msg_standard_layout) {
            //标准分布
            d(TAG, "case:$msgId should set the standardLayout...")
            standardLayout(treeView, nodeView as NodeView)
        } else if (msgId == msg_correct_layout) {
            //纠正
            d(TAG, "case:$msgId should do correctTreeLayout")
            correctTreeLayout(treeView, nodeView as NodeView)
        } else if (msgId == msg_box_call_back) {
            d(TAG, "case:$msgId,should do call back ")
            //根据此时的TreeView的大小变化,设置TreeViewSize
            /**
             * android.view.ItemView
             * public final int getLeft()
            Left position of this view relative to its parent.*/
//            view!!.let {  }
            var leftNew: Int
            var topNew: Int
            var bottomNew: Int
            var rightNew: Int
            with(nodeView!!) {
//                val leftKK=left
                leftNew = left
                topNew = top
                bottomNew = bottom
                rightNew = right
            }
            if (leftNew < mTreeViewSize!!.left) {
                mTreeViewSize.left = leftNew
            }
            if (topNew < mTreeViewSize.top) {
                mTreeViewSize.top = topNew
            }
            if (bottomNew > mTreeViewSize.bottom) {
                mTreeViewSize.bottom = bottomNew
            }
            if (rightNew > mTreeViewSize.right) {
                mTreeViewSize.right = rightNew
            }
        }
    }


    /**
     * 标准分布
     *
     * @param treeView
     * @param rootView
     */
    private fun standardLayout(treeView: TreeView?, rootView: NodeView) {
        d(TAG, "applying the standard treeLayout...")
        val treeNode = rootView.treeNode
        if (treeNode != null) {
            //所有的子节点
            val childNodes = treeNode.childNodes
            val size = childNodes.size
            val mid = size / 2
            val r = size % 2

            //base line
            val left = rootView.right + mDx
            var top = rootView.top + rootView.measuredHeight / 2
            val right: Int
            val bottom: Int
            if (size == 0) {
                return
            } else if (size == 1) {
                val midChildNodeView = treeView!!.findNodeViewFromNodeModels(childNodes[0]) as NodeView
                top -= midChildNodeView.measuredHeight / 2
                right = left + midChildNodeView.measuredWidth
                bottom = top + midChildNodeView.measuredHeight
                midChildNodeView.layout(left, top, right, bottom)
            } else {
                var topTop = top
                var topRight: Int
                var topBottom: Int
                var bottomTop = top
                var bottomRight: Int
                var bottomBottom: Int
                if (r == 0) { //偶数
                    for (i in mid - 1 downTo 0) {
                        val topView = treeView!!.findNodeViewFromNodeModels(childNodes[i]) as NodeView
                        val bottomView = treeView.findNodeViewFromNodeModels(childNodes[size - i - 1]) as NodeView
                        if (i == mid - 1) {
                            topTop = topTop - mDy / 2 - topView.measuredHeight
                            topRight = left + topView.measuredWidth
                            topBottom = topTop + topView.measuredHeight
                            bottomTop += mDy / 2
                            bottomRight = left + bottomView.measuredWidth
                            bottomBottom = bottomTop + bottomView.measuredHeight
                        } else {
                            topTop = topTop - mDy - topView.measuredHeight
                            topRight = left + topView.measuredWidth
                            topBottom = topTop + topView.measuredHeight
                            bottomTop += mDy
                            bottomRight = left + bottomView.measuredWidth
                            bottomBottom = bottomTop + bottomView.measuredHeight
                        }
                        topView.layout(left, topTop, topRight, topBottom)
                        bottomView.layout(left, bottomTop, bottomRight, bottomBottom)
                        bottomTop = bottomView.bottom
                    }
                } else {
                    val midView = treeView!!.findNodeViewFromNodeModels(childNodes[mid]) as NodeView
                    midView.layout(
                        left, top - midView.measuredHeight / 2, left + midView.measuredWidth,
                        top - midView.measuredHeight / 2 + midView.measuredHeight
                    )
                    topTop = midView.top
                    bottomTop = midView.bottom
                    for (i in mid - 1 downTo 0) {
                        val topView = treeView.findNodeViewFromNodeModels(childNodes[i]) as NodeView
                        val bottomView = treeView.findNodeViewFromNodeModels(childNodes[size - i - 1]) as NodeView
                        topTop = topTop - mDy - topView.measuredHeight
                        topRight = left + topView.measuredWidth
                        topBottom = topTop + topView.measuredHeight
                        bottomTop += mDy
                        bottomRight = left + bottomView.measuredWidth
                        bottomBottom = bottomTop + bottomView.measuredHeight
                        topView.layout(left, topTop, topRight, topBottom)
                        bottomView.layout(left, bottomTop, bottomRight, bottomBottom)
                        bottomTop = bottomView.bottom
                    }
                }
            }
        }
    }

    /**
     * 移动
     *
     * @param rootView
     * @param dy
     */
    private fun moveNodeLayout(superTreeView: TreeView?, rootView: NodeView, dy: Int) {
        /*LocalRootView*/
        var lRootView = rootView
        val queue: Deque<Node<String>?> = ArrayDeque()
        var rootNode = lRootView.treeNode
        queue.add(rootNode)
        while (!queue.isEmpty()) {
            rootNode = queue.poll()
            lRootView = superTreeView!!.findNodeViewFromNodeModels(rootNode) as NodeView
            val l = lRootView.left
            val t = lRootView.top + dy
            lRootView.layout(l, t, l + lRootView.measuredWidth, t + lRootView.measuredHeight)
            val childNodes = rootNode!!.childNodes
            for (item in childNodes) {
                queue.add(item)
            }
        }
    }

    /**
     * root节点的定位
     *
     * @param rootNodeView
     */
    private fun setRootNodeViewLayout(rootNodeView: NodeView) {
        val lr = mDy
        val tr = mDx

        /**Like getMeasuredWidthAndState(),
         * but only returns the raw width component (that is the result is masked by MEASURED_SIZE_MASK).*/
        val rr = lr + rootNodeView.measuredWidth
        val br = tr + rootNodeView.measuredHeight
        /**
         * android.view.View public void layout(int l, int t, int r,int b)
        
        Assign a size and position to a view and all of its descendants
        This is the second phase(阶段) of the layout mechanism. (The first is measuring).
        In this phase, each parent calls layout on all of its children to position them. This is typically done using the child measurements that were stored in the measure pass().
        Derived classes should not override this method. Derived classes with children should override onLayout. In that method, they should call layout on each of their children.*/
        rootNodeView.layout(lr, tr, rr, br)
    }

    init {
        mTreeViewSize = TreeViewSize()
        mDx = dx
        mDy = dy
        mHeight = height
    }
}