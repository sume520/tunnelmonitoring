package com.sun.tunnelmonitoring.tree


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView

import com.sun.tunnelmonitoring.R
import java.util.ArrayList

class TreeFragment : Fragment() {

    private var lv_tree: ListView? = null
    private var adapter: TreeNodeAdapter? = null
    private var parent: TreeNode? = null
    private val patentList = ArrayList<TreePatent>()
    private val mList = ArrayList<TreeInfo>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        var view=inflater.inflate(R.layout.fragment_tree, container, false)
        return view
    }

    companion object {

        @JvmStatic
        fun newInstance() =
                TreeFragment()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initData()
        initView()
        addListener()

    }

    private fun initData() {
        //组
        //TreePatent patent1 = new TreePatent(1, 0, "大坡塘2号隧道");//id, parentId, name
        val patent2 = TreePatent(2, 0, "大坡塘3号隧道")
        val patent3 = TreePatent(3, 0, "川坡坳隧道")
        val patent4 = TreePatent(4, 0, "泮春隧道")
        val patent5 = TreePatent(5, 0, "连云山隧道5标")
        val patent6 = TreePatent(6, 0, "连云山隧道6标")
        val patent7 = TreePatent(7, 0, "隧道1")
        val patent8 = TreePatent(8, 0, "隧道2")
        val patent9 = TreePatent(9, 0, "隧道3")
        val patent10 = TreePatent(10, 2, "大坡塘3号隧道正洞")
        val patent11 = TreePatent(11, 4, "泮春隧道出口")
        val patent12 = TreePatent(12, 0, "隧道4")
        val patent13 = TreePatent(13, 0, "隧道5")
        val patent14 = TreePatent(14, 0, "隧道6")
        val patent15 = TreePatent(15, 0, "隧道7")


        //patentList.add(patent1);
        patentList.add(patent2)
        patentList.add(patent3)
        patentList.add(patent4)
        patentList.add(patent5)
        patentList.add(patent6)
        patentList.add(patent7)
        patentList.add(patent8)
        patentList.add(patent9)
        patentList.add(patent10)
        patentList.add(patent11)

        //组对应节点
        val info0 = TreeInfo(7, 0, "大坡塘2号隧道")
        val info1 = TreeInfo(0, 3, "川坡坳隧道正洞")//id, groupId, name
        val info2 = TreeInfo(1, 10, "测试断面图1")
        val info3 = TreeInfo(2, 4, "泮春隧道进口")
        val info4 = TreeInfo(3, 11, "213")
        val info5 = TreeInfo(4, 2, "连云山隧道进口")
        val info6 = TreeInfo(5, 3, "连云山隧道1#横通道")
        val info7 = TreeInfo(6, 4, "连云山隧道出口")
        mList.add(info0)
        mList.add(info1)
        mList.add(info2)
        mList.add(info3)
        mList.add(info4)
        mList.add(info5)
        mList.add(info6)
        mList.add(info7)
        parent = TreeNode(null, "隧道列表", false)
        initTreeRoot(parent!!, 0)
    }

    //添加分支节点
    private fun initTreeRoot(parent: TreeNode, parentId: Int) {
        for (i in patentList.indices) {
            val treeParent = patentList[i]
            if (parentId == treeParent.parentId) {
                val treeNode = TreeNode(parent, treeParent.name, false)
                initTreeRoot(treeNode, treeParent.id)
                parent.addChildNode(treeNode)
            }
        }
        initTreeChild(parent, parentId)
    }

    //添加子叶节点
    private fun initTreeChild(parent: TreeNode, groupId: Int) {
        for (i in mList.indices) {
            val treeInfo = mList[i]
            if (groupId == treeInfo.groupId) {
                val treeNode = TreeNode(parent, treeInfo.name, true)
                parent.addChildNode(treeNode)
            }
        }
    }

    private fun initView() {
        lv_tree = activity!!.findViewById(R.id.lv_tree) as ListView?
        adapter = TreeNodeAdapter(context!!, parent!!)
        lv_tree!!.adapter = adapter
    }

    private fun addListener() {
        lv_tree!!.setOnItemClickListener { parent, view, position, id ->
            adapter!!.OnClickListener(
                position,
                false,
                false,
                ""
            )
        }
    }
}
