package com.sun.tunnelmonitoring.tree

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import android.widget.TextView
import com.sun.tunnelmonitoring.R

import java.util.ArrayList

/**
 * Created by ZBL on 2018/12/12.
 */

class TreeNodeAdapter(private val mContext: Context, private val root: TreeNode) : BaseAdapter() {
    private val mInflater: LayoutInflater

    private val mListNode = ArrayList<TreeNode>()//所有的节点
    private val mListTree = ArrayList<TreeNode>()//要展现的节点

    init {
        mInflater = LayoutInflater.from(mContext)
        mListNode.clear()
        setTreeNode(root)
        setTreeNodeToShow(false, false, "")
    }

    /***
     * 添加所有节点到mListNode列表中
     * @param node
     */
    fun setTreeNode(node: TreeNode) {
        mListNode.add(node)
        if (node.isLeaf) return
        val child = node.childList ?: return
        for (i in child.indices) {
            setTreeNode(child[i])
        }
    }

    private fun setTreeNodeToShow(isCheck: Boolean, state: Boolean, name: String?) {
        mListTree.clear()
        treeNodeToShow(this.root, isCheck, state, name)
    }

    /**
     * 装配所有展开的节点数据显示出来
     * @param node
     * @param isCheck
     * @param state
     * @param name
     */
    private fun treeNodeToShow(node: TreeNode, isCheck: Boolean, state: Boolean, name: String?) {
        mListTree.add(node)
        if (node.isExpanded
            && !node.isLeaf
            && node.childList != null
        ) {
            val children = node.childList
            for (i in children!!.indices) {
                if (state && node.name == name) {
                    node.isCheck = isCheck
                    children[i].isCheck = isCheck
                    changceCheck(children[i], state)
                }
                treeNodeToShow(children[i], isCheck, state, name)
            }
        }
    }

    /**
     * 改变子节点状态
     * @param node
     * @param state
     */
    fun changceCheck(node: TreeNode, state: Boolean) {
        if (state) {
            val child = node.childList
            if (child != null) {
                for (j in child.indices) {
                    child[j].isCheck = node.isCheck
                    changceCheck(child[j], state)
                }
            }
        }
    }

    /**
     * 改变展开/收起状态
     * @param node
     * @param isCheck
     * @param state
     */
    fun changceExpandedTree(node: TreeNode, isCheck: Boolean, state: Boolean) {
        for (i in mListNode.indices) {
            if (node.name == mListNode[i].name) {
                if (state) {
                    mListNode[i].isCheck = isCheck
                } else {
                    val flag = mListNode[i].isExpanded
                    mListNode[i].isExpanded = !flag
                }
            }
        }
    }

    /**
     * 点击item
     * @param isCheck
     * @param state  true位点击ChechBox， false为点击item
     * @param name
     */
    fun OnClickListener(position: Int, isCheck: Boolean, state: Boolean, name: String?) {
        val node = mListTree[position]
        if (node.isLeaf) {
            if (state) {
                for (i in mListNode.indices) {
                    if (node.name == mListNode[i].name) {
                        mListNode[i].isCheck = isCheck
                    }
                }
            }
        } else {
            changceExpandedTree(node, isCheck, state)
            setTreeNodeToShow(isCheck, state, name)
            notifyDataSetChanged()
        }
    }

    override fun getCount(): Int {
        return mListTree.size
    }

    override fun getItem(position: Int): Any {
        return mListTree[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        val holder: ViewHolder
        if (convertView == null) {
            holder = ViewHolder()
            convertView = mInflater.inflate(R.layout.tree_item, null)
            holder.cb_check = convertView!!.findViewById(R.id.cb_check)
            holder.tv_expanded = convertView.findViewById(R.id.tv_expanded)
            holder.tv = convertView.findViewById(R.id.tv)
            convertView.tag = holder
        } else {
            holder = convertView.tag as ViewHolder
        }

        val node = mListTree[position]
        holder.cb_check!!.isChecked = node.isCheck

        if (!node.isLeaf) {
            if (node.isExpanded) {
                holder.tv_expanded!!.visibility = View.VISIBLE
                holder.tv_expanded!!.text = "-"
            } else {
                holder.tv_expanded!!.visibility = View.VISIBLE
                holder.tv_expanded!!.text = "+"
            }
        } else {
            holder.tv_expanded!!.visibility = View.GONE
        }
        holder.tv!!.text = node.name

        holder.cb_check!!.setOnClickListener { OnClickListener(position, holder.cb_check!!.isChecked, true, node.name) }

        convertView.setPadding(node.level * 50, 0, 0, 0)//根据节点树级排布节点位置
        return convertView
    }

    internal inner class ViewHolder {
        var cb_check: CheckBox? = null
        var tv_expanded: TextView? = null
        var tv: TextView? = null
    }

}
