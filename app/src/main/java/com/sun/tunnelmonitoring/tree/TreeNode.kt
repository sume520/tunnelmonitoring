package com.sun.tunnelmonitoring.tree

import java.util.ArrayList

/**
 * Created by ZBL on 2018/12/12.
 */

class TreeNode(
    treeNode: TreeNode?, var name: String?//名称
    , isLeaf: Boolean
) {

    var parent: TreeNode? = null//父节点
    var isLeaf = false//是否为叶节点
    var isExpanded = false//该节点是否展开
    var childList: ArrayList<TreeNode>? = null//子树
    var isCheck = false

    //得到当前节点所在的层数，根为0层
    val level: Int
        get() = if (parent == null) 0 else parent!!.level + 1

    init {
        this.parent = treeNode
        this.isLeaf = isLeaf
    }

    //添加子节点
    fun addChildNode(node: TreeNode) {
        if (childList == null) {
            childList = ArrayList()
        }
        childList!!.add(node)
    }

    //清空子节点
    fun clearChildren() {
        if (childList != null) {
            childList!!.clear()
        }
    }

}

