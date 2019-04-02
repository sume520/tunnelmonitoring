package com.sun.tunnelmonitoring.projectTree


import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.squareup.okhttp.*
import com.sun.tunnelmonitoring.MyApplication
import com.sun.tunnelmonitoring.PROJECTTREEURL
import com.sun.tunnelmonitoring.R
import com.sun.tunnelmonitoring.RefreshFragment
import com.sun.tunnelmonitoring.events.ItemNameEvent
import com.sun.tunnelmonitoring.events.RefreshEvent
import kotlinx.android.synthetic.main.fragment_tree.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.*

class TreeFragment : Fragment(), View.OnClickListener {

    private var lv_tree: ListView? = null
    private var adapter: TreeNodeAdapter? = null
    private var parent: TreeNode? = null
    private val parentList = ArrayList<TreeParent>()
    private val mList = ArrayList<TreeInfo>()
    private var tree_jsonString: String? = null
    private var tree_object: JSONObject? = null
    internal val tree_client = OkHttpClient()
    private var itemName = ""
    private val url = "http://future.ngrok.xiaomiqiu.cn/app/tree"

    private val treeHandler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(tree_msg: Message) {
            val tree_Message = tree_msg.obj as String
            if (tree_Message != "10") {
                TreeData.isPullData = true
                TreeData.treeJson = tree_Message
                refreshUI()
            } else {
                Toast.makeText(MyApplication.getContext(), tree_Message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    @SuppressLint("ResourceType", "LongLogTag")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: ItemNameEvent) {
        itemName = event.itemName
        Log.i("TreeFragment->onMessageEvent", "点击：$itemName")

        val fragment = ProjectInfoFragment()
        val bundle = Bundle()
        bundle.putString("itemName", itemName)
        fragment.arguments = bundle

        activity!!.supportFragmentManager
            .beginTransaction().add(R.id.fg_tree, fragment)
            .addToBackStack(null)
            .commit()
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var instance: TreeFragment? = null
            get() {
                if (field == null) {
                    field = TreeFragment()
                }
                return field
            }

        @Synchronized
        fun get(): TreeFragment {//单例模式
            return instance!!
        }
    }

    private fun refreshUI() {//刷新UI

        activity!!.supportFragmentManager
            .beginTransaction().replace(R.id.fg_tree, RefreshFragment())
            .commit()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        EventBus.getDefault().register(this)
        EventBus.getDefault().post(RefreshEvent(false))
        val view = inflater.inflate(R.layout.fragment_tree, container, false)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initData()
        initView()
        addListener()

    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.bt_treeUpdate -> postRequest("工程树", itemName)
            R.id.bt_treeDelete -> postRequest("删除", itemName)
        }
    }

    //初始化工程树数据
    private fun initData() {
        if (TreeData.isPullData) {//如果isPullData为true，刷新界面
            val tree_data = TreeData.treeJson
            val gson = Gson()
            val jsonParser = JsonParser()
            val jsonElements = jsonParser.parse(tree_data).getAsJsonArray()
            val tree_news = ArrayList<Tree_news>()

            //防止重复添加
            parentList.clear()
            mList.clear()

            for (tree_new in jsonElements) {
                val tree_news1 = gson.fromJson<Tree_news>(tree_new, Tree_news::class.java)
                tree_news.add(tree_news1)
            }
            var num = 0
            while (num < tree_news.size) {
                for (i in tree_news.indices) {
                    if (tree_news[num].id == tree_news[i].parent) {
                        parentList.add(
                            TreeParent(
                                tree_news[num].id,
                                tree_news[num].parent,
                                tree_news[num].name
                            )
                        )
                        break
                    }
                    if (i == tree_news.size - 1) {
                        mList.add(TreeInfo(tree_news[num].id, tree_news[num].parent, tree_news[num].name))
                    }
                }
                num++
            }
            TreeData.isPullData = false
        }
        parent = TreeNode(null, "隧道列表", false)
        initTreeRoot(parent!!, 0)
    }

    //添加分支节点
    private fun initTreeRoot(parent: TreeNode, parentId: Int) {
        for (i in parentList.indices) {
            val treeParent = parentList[i]
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
        bt_treeUpdate.setOnClickListener(this)
        bt_treeDelete.setOnClickListener(this)
    }

    private fun addListener() {
        lv_tree!!.setOnItemClickListener { _, _, position, _ ->
            adapter!!.OnClickListener(
                position,
                false,
                false,
                ""
            )
        }
    }

    /*
    post请求后台
     */
    private fun postRequest(name: String, ItemName: String) {

        //建立请求表单，添加上传服务器的参数
        tree_object = null
        tree_object = JSONObject()
        try {
            tree_object!!.put(name, 0)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        tree_jsonString = null
        tree_jsonString = tree_object!!.toString()
        EventBus.getDefault().post(RefreshEvent(true))
        val body = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), tree_jsonString)
        val request = Request.Builder()
            .url(PROJECTTREEURL)
            .post(body)
            .build()
        //新建一个线程，用于得到服务器响应的参数
        Thread {
            val response: Response?
            try {
                //回调
                response = tree_client.newCall(request).execute()
                if (response!!.isSuccessful) {
                    //将服务器响应的参数response.body().string())发送到hanlder中，并更新ui
                    treeHandler.obtainMessage(1, response.body().string()).sendToTarget()
                } else {
                    throw IOException("Unexpected code:$response")
                }
            } catch (e: IOException) {
                e.printStackTrace()
                EventBus.getDefault().post(RefreshEvent(flag = false))
                treeHandler.post { Toast.makeText(MyApplication.getContext(), "获取工程树数据失败", Toast.LENGTH_SHORT).show() }
            }
        }.start()

        pb_pulldata.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        EventBus.getDefault().unregister(this)
    }
}
