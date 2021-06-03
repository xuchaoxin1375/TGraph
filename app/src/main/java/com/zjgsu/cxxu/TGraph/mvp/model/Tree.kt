
import com.zjgsu.cxxu.TGraph.Lg
import com.zjgsu.cxxu.TGraph.Lg.d
import com.zjgsu.cxxu.TGraph.mvp.model.Node
import com.zjgsu.cxxu.TGraph.mvp.model.TreeItem
import java.io.*
import java.util.*


class Tree<T>(rootNode: Node<T>) : Serializable {
    /**
     * the root for the tree
     *
     * the assignment can be write in/complete by init{} structure
     * there,you don't have to set a value to mRootNode or use lateinit
     */
    companion object {

        private const val TAG = "Tree"
    }

    private var mRootNode: Node<T> = rootNode

    /*store structure member*/
    @Transient
    private var mTreeItem: TreeItem<Node<T>>? = null

    /**
     * add the node to the its parent node
     * there are two case to add an node:
     * the new node as a child node /a brother node
     *
     * @param start the start node to add node(brother node or child node)
     * @param nodes the node instance to be add to the tree(there you make pass several nodes to the method)
     */
    fun addNode(start: Node<T>, vararg nodes: Node<T>) {
        Lg.d(TAG, "node${nodes} were added just before")
        var index = 1
        val temp: Node<T> = start
        if (temp.parentNode != null) {
            index = temp.parentNode!!.floor
        }
        val childNodes: LinkedList<Node<T>> = temp.childNodes
        for (t in nodes) {
            t.parentNode = start
            t.floor = index

            //校验是否存在
            var exist = false
            for (hash in childNodes) {
                if (hash === t) {
                    exist = true
                    continue
                }
            }
            if (!exist) start.childNodes.add(t)
        }
    }

    /*remove tree node,and return the removed node*/
    fun removeNode(startNode: Node<T>, deleteNode: Node<T>?): Boolean {
        Lg.d(TAG, "remove the node:$deleteNode from:$startNode")
        var isRemoved = false
        val size: Int = startNode.childNodes.size
        if (size > 0) {
            /**
             * kotlin.collections CollectionsKt.class @InlineOnly
            public inline fun <@OnlyInputTypes T> MutableCollection<out T>.remove(element: T): Boolean
             * Removes a single instance of the specified element from this collection, if it is present.*/
            isRemoved = startNode.childNodes.remove(deleteNode)
        }
        return isRemoved
    }


    fun getRootNode(): Node<T>{
        return mRootNode
    }

    /**
     * 同一个父节点的上下
     *
     * @param midPreNode
     * @return
     */
    private fun getLowNode(midPreNode: Node<T>): Node<T>? {
        Lg.d(TAG, "getting the LowNode...")
        var find: Node<T>? = null
        val parentNode: Node<T>? = midPreNode.parentNode
        if (parentNode != null && parentNode.childNodes.size >= 2) {
            val queue: Deque<Node<T>> = ArrayDeque<Node<T>>()
            var rootNode: Node<T> = parentNode
            queue.add(rootNode)
            var up = false
            while (!queue.isEmpty()) {
                rootNode = queue.poll() as Node<T>
                if (up) {
                    if (rootNode.floor === midPreNode.floor) {
                        find = rootNode
                    }
                    break
                }

                if (rootNode === midPreNode) up = true
                val childNodes: LinkedList<Node<T>> = rootNode.childNodes
                if (childNodes.size > 0) {
                    for (item in childNodes) {
                        queue.add(item)
                    }
                }
            }
        }
        return find
    }

    fun getAllLowNodes(addNode: Node<T>): ArrayList<Node<T>> {
        Lg.d(TAG, "collecting all low Nodes..")
        val array: ArrayList<Node<T>> = ArrayList<Node<T>>()
        var parentNode: Node<T>? = addNode.parentNode
        while (parentNode != null) {
            var lowNode: Node<T>? = getLowNode(parentNode)
            while (lowNode != null) {
                array.add(lowNode)
                lowNode = getLowNode(lowNode)
            }
            parentNode = parentNode.parentNode
        }
        Lg.d(TAG, "get ${array.size} nodes.")
        return array
    }

    /**
     * we try to regard the Node as a certain midNode:
     * @return Node<T>?
     */
    private fun getPreNodeBFS(midPreNode: Node<T>): Node<T>? {
        Lg.d(TAG, "get preNode by BFS for the midPreNode:value=${midPreNode.value}")
        val parentNode = midPreNode.parentNode
        var findNode: Node<T>? = null
        /*prepare to traverse search:*/
        if (parentNode != null && parentNode.childNodes.size > 0) {
            /*execute the BFS search algorithm:(with deque):*/
            /**A linear collection that supports element insertion and removal at both ends.
             *  The name deque is short for "double ended queue" and is usually pronounced "deck".*/
            val queue: Deque<Node<T>> = ArrayDeque<Node<T>>()
            var rootNode: Node<T> = parentNode

            queue.add(rootNode)
            while (!queue.isEmpty()) {
                /**Retrieves and removes the head of the queue represented by this deque (in other words, the first element of this deque), or returns null if this deque is empty.
                 * there it won't return the null,becasue we do the judge ,the queue there is not empty.*/
                rootNode = queue.poll() as Node<T>
                //找到该元素
                if (rootNode === midPreNode) {
                    //返回之前的值
                    break
                }
                findNode = rootNode
                val childNodes: LinkedList<Node<T>> = rootNode.childNodes
                if (childNodes.size > 0) {
                    for (item in childNodes) {
                        queue.add(item)
                    }
                }
            }
            if (findNode != null && findNode.floor !== midPreNode.floor) {
                findNode = null
            }
        }
        Lg.d(TAG,"get the preNode:value=${findNode?.value}")
        return findNode
    }

    fun getAllPreNodes(addNode: Node<T>): ArrayList<Node<T>> {
        Lg.d(TAG, "getting all preNodes...")
        val preNodesArray: ArrayList<Node<T>> = ArrayList()
        var parentNode = addNode.parentNode
        while (parentNode != null) {
            var lowNode: Node<T>? = getPreNodeBFS(parentNode)
            while (lowNode != null) {
                //syntactic sugar for add
                preNodesArray += lowNode
                lowNode = getPreNodeBFS(lowNode)
                Lg.d(TAG, "get the preNode searching:value=${lowNode?.value}")
            }
            parentNode = parentNode.parentNode
        }
        return preNodesArray
    }


    /*traverse tree in deep,this will set the mForTreeItem*/
    fun traverseTreeInDeep(msgId: Int) {
        Lg.d(TAG, "traversing the tree in deep... ")
        val stack: Stack<Node<T>> = Stack()
        //with the type inference:
        val rootNode = getRootNode()
        /*we use the stack to implement the traverse in deep
        * what's more,you can use a queue to implement the traverse in width*/
        stack.add(rootNode)
        while (!stack.isEmpty()) {
            /**
             * java.util.Stack<E> public E pop()
            Removes the object at the top of this stack and
            @returns that object as the value of this function.*/
            val poppedNode: Node<T> = stack.pop()
            /*test the function with the time to know that which(how many) nodes were popped at the invoke time(while loop)*/
            d(TAG,"detected the Node popped:value:${poppedNode.value}")
            mTreeItem?.setNext(msgId, poppedNode)
            val childNodes: LinkedList<Node<T>> = poppedNode.childNodes
            for (item in childNodes) {
                stack.add(item)
            }
        }
        d(TAG,"the stack is empty now...")
    }

    fun traverseTreeInWidth(msg: Int) {
        val queue: Deque<Node<T>> = ArrayDeque<Node<T>>()
        var rootNode: Node<T>? = getRootNode()
        queue.add(rootNode)
        while (!queue.isEmpty()) {
            rootNode = queue.poll() as Node<T>
//            if (mForTreeItem != null) {
//            }
            mTreeItem?.setNext(msg, rootNode)
            val childNodes: LinkedList<Node<T>> = rootNode.childNodes
            if (childNodes.size > 0) {
                for (item in childNodes) {
                    queue.add(item)
                }
            }
        }
    }

    /**
     * set the member mTreeItem with the param TreeItem:
     * @param treeItem<Node<T>>?
     * why not use the Node<T> even Node<String> as the parameter directly?
     * you could,but use the TreeItem<Node<T>>? this could remind you that Node should implement the next()
     * the idea provide us a new way to extend the function of the Node class*/
    fun setTreeItem(treeItem: TreeItem<Node<T>>?) {
        mTreeItem = treeItem
    }

    @Throws(IOException::class, ClassNotFoundException::class)
    fun deepClone(): Any {
        val bo = ByteArrayOutputStream()
        val oo = ObjectOutputStream(bo)
        oo.writeObject(this)
        val bi = ByteArrayInputStream(bo.toByteArray())
        val oi = ObjectInputStream(bi)
        return oi.readObject()
    }

}