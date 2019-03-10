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
import com.sun.tunnelmonitoring.R
import com.sun.tunnelmonitoring.RefreshFragment
import kotlinx.android.synthetic.main.fragment_tree.*
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
    private val patentList = ArrayList<TreePatent>()
    private val mList = ArrayList<TreeInfo>()
    private var tree_jsonString: String? = null
    private var tree_object: JSONObject? = null
    internal val tree_client = OkHttpClient()
    private var itemName = ""
    private val url = "http://47.107.158.26:80/app/tree"
    private var flag = false

    private val tree_messageHandler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(tree_msg: Message) {
            val tree_Message = tree_msg.obj as String
            if (tree_Message != "10") {
                TreeData.isPullData = true
                TreeData.treeJson = tree_Message
                Log.i(">>>>>>>>>>>>>>>>", tree_Message)
                refreshUI()
            } else {
                Toast.makeText(MyApplication.getContext(), tree_Message, Toast.LENGTH_SHORT).show()
            }

        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: ItemNameEvent) {
        itemName = event.itemName
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
        if (TreeData.isPullData) {//如果flag为true，刷新界面
            val tree_data = TreeData.treeJson
            val gson = Gson()
            val jsonParser = JsonParser()
            val jsonElements = jsonParser.parse(tree_data).getAsJsonArray()
            val tree_news = ArrayList<Tree_news>()

            //防止重复添加
            tree_news.clear()
            patentList.clear()
            mList.clear()

            for (tree_new in jsonElements) {
                val tree_news1 = gson.fromJson<Tree_news>(tree_new, Tree_news::class.java)
                tree_news.add(tree_news1)
            }
            Log.i(">>>>>>>>>>", tree_news.size.toString() + "")
            //Log.i("tree_news", tree_news + "")
            var num = 0
            while (num < tree_news.size) {
                for (i in tree_news.indices) {
                    if (tree_news[num].id == tree_news[i].parent) {
                        patentList.add(TreePatent(tree_news[num].id, tree_news[num].parent, tree_news[num].name))
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
        bt_treeUpdate.setOnClickListener(this)
        bt_treeDelete.setOnClickListener(this)
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
        val body = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), tree_jsonString)
        val request = Request.Builder()
            .url(url)
            .post(body)
            .build()
        //新建一个线程，用于得到服务器响应的参数
        Thread {
            var tree_response: Response? = null
            try {
                //回调
                tree_response = tree_client.newCall(request).execute()
                if (tree_response!!.isSuccessful()) {
                    //将服务器响应的参数response.body().string())发送到hanlder中，并更新ui
                    tree_messageHandler.obtainMessage(1, tree_response.body().string()).sendToTarget()
                } else {
                    throw IOException("Unexpected code:" + tree_response)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }.start()

        pb_pulldata.visibility = View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()
        //EventBus.getDefault().unregister(this)
    }
}
