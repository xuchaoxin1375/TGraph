package com.zjgsu.cxxu.TGraph.mvp.model

import java.io.Serializable

/**
 * the interface named TreeItem,the generic type T must be extends from Node<*>?(such as Node<String>?
 * the interface is a generic interface,with the generic type T,the T meet <T:Node<*>?>
 *     So,although I set the type as Node<*>?,but generally,it is Node<String>
 *
 *
 *     the abstract method define in the interface with the parameter (or return value) type of T
 *The TreeItem may be understand as the tree Node container(holder)
 * public interface ForTreeItem<T extends Node<?>> extends Serializable{
void next(int msg, T next);
}
 */
interface TreeItem<T : Node<*>?> : Serializable {
    /*aha,the parameter nextNode.*/
    fun setNext(msgId: Int, nextNode: T)
}