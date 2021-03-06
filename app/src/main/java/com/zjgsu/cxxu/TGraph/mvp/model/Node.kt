package com.zjgsu.cxxu.TGraph.mvp.model

import java.io.Serializable
import java.util.*

class Node<T>(
    /**
     * the data value
     */
    var value: T
) : Serializable {
    /*java bean:*/
    /**
     * the parent node,the root node's parent node=null;
     * the node type is a generic type:Node<T>
    </T> */
    var parentNode: Node<T>?
    /**
     * setValue of the Node */

    /**
     * if the node has the child nodes,saved  in the LinkedList<Node></Node><T>>
    </T> */
    var childNodes: LinkedList<Node<T>>
    /**
     * transient(短暂) 关键字的作用及使用方法
     * 这个字段的生命周期仅存于调用者的内存中而不会写到磁盘里持久化
     * 我们都知道一个对象只要实现了Serilizable接口，这个对象就可以被序列化，java的这种序列化模式为开发者提供了很多便利，我们可以不必关系具体序列化的过程，只要这个类实现了Serilizable接口，这个类的所有属性和方法都会自动序列化。
     *
     * 然而在实际开发过程中，我们常常会遇到这样的问题，这个类的有些属性需要序列化，而其他属性不需要被序列化，
     * 打个比方，如果一个用户有一些敏感信息（如密码，银行卡号等），为了安全起见，不希望在网络操作（主要涉及到序列化操作，本地序列化缓存也适用）中被传输，这些信息对应的变量就可以加上transient关键字。
     * 换句话说，这个字段的生命周期仅存于调用者的内存中而不会写到磁盘里持久化。
     *
     * 总之，java 的transient关键字为我们提供了便利，你只需要实现Serilizable接口，将不需要序列化的属性前添加关键字transient，序列化对象的时候，这个属性就不会序列化到指定的目的地中。
     */
    /**
     * focus tag for the tree add nodes
     */
    @Transient
    var isFocus: Boolean

    /**
     * index of the tree floor
     */
    var floor = 0
    var hidden = false

    /*构造函数:*/
    init {
        childNodes = LinkedList()
        /*default the node is not focused*/isFocus = false
        parentNode = null
    }
}