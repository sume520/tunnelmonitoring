package com.sun.tunnelmonitoring.projectTree


import android.annotation.SuppressLint
import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.google.gson.Gson
import com.squareup.okhttp.Request
import com.sun.tunnelmonitoring.R
import com.zhy.http.okhttp.OkHttpUtils
import com.zhy.http.okhttp.callback.StringCallback
import kotlinx.android.synthetic.main.fragment_project_info.*
import lecho.lib.hellocharts.gesture.ContainerScrollType
import lecho.lib.hellocharts.gesture.ZoomType
import lecho.lib.hellocharts.model.*

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class ProjectInfoFragment : Fragment(), AdapterView.OnItemSelectedListener {
    private lateinit var dataType: Array<String>
    private lateinit var itemName: String
    private lateinit var axisXname: String
    private lateinit var axisYname: String
    val colors =
        arrayOf(0xFF2196F3.toInt(), 0xFF66BB6A.toInt(), 0xFF673AB7.toInt(), 0xFFFFEB3B.toInt())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        itemName = arguments!!.getString("itemName")
        Log.i("onCreateView", "itemName: $itemName")
        dataType = resources.getStringArray(R.array.data_types)
        return inflater.inflate(R.layout.fragment_project_info, container, false)
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val type = dataType[position]
        Log.i("onItemSelected", "position: $position, id: $id, data type: $type")
        progressBar.visibility = View.VISIBLE
        OkHttpUtils
            .post()
            .url("http://future.ngrok.xiaomiqiu.cn/app/data")
            .addParams("number", itemName)
            .addParams("type", type)
            .build()
            .execute(DataCallback())
        axisXname = type
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //下拉列表适配器
        val adapter = ArrayAdapter.createFromResource(
            context,
            R.array.data_types, android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spiner.adapter = adapter
        spiner.onItemSelectedListener = this
    }

    inner class DataCallback : StringCallback() {
        override fun onResponse(response: String?) {
            Log.i("DataCallback", response)
            val gson = Gson()
            val datas = gson.fromJson(response, SensorDataList::class.java)
            datas.data.forEach {
                println(it.time + " " + it.data)
            }
            setChartData(datas.data)
        }

        override fun onError(request: Request?, e: Exception?) {
            Log.e("DataCallback", e!!.message)
            Toast.makeText(context, "获取数据失败", Toast.LENGTH_SHORT).show()
        }
    }

    private fun initChart() {
        setViewPort()
    }

    @SuppressLint("CheckResult")
    private fun setChartData(datas: List<SensorData>) {
        val numberOfPoints = datas.size
        val lines = ArrayList<Line>()
        var maxValue = 0

        initChart()

        val times = mutableListOf<String>()
        val dates = mutableListOf<String>()

        for (data in datas) {//提取时间数组
            val strs = data.time.trim().split(" ")
            dates.add(strs[0])
            times.add(strs[1].substring(0, 5))
            if (data.data > maxValue)
                maxValue = data.data
            Log.i("setChart", "date: ${strs[0]} time: ${strs[1]}")
        }

        //x轴标签
        val xLabels = ArrayList<String>()
        for (i in 0 until numberOfPoints) {
            var str: String

            if (times[i] == "12:00")
                str = dates[i].substring(5)
            else
                str = times[i]
            xLabels.add(str)
        }

        //填充数据
        //values!!.clear()//清空数值避免出现线条重叠
        val maxNumberOfLines = 1
        for (lineNumber in 0 until maxNumberOfLines) {
            val line = Line()
            val values = ArrayList<PointValue>()
            for (pointNumber in 0 until numberOfPoints) {
                values.add(PointValue(pointNumber.toFloat(), datas[pointNumber].data.toFloat()))
            }
            line.values = values
            line.color = colors[lineNumber]
            line.isCubic = true
            line.isFilled = true
            line.setHasPoints(false)
            lines.add(line)
        }

        drawChart(xLabels, lines, numberOfPoints, maxValue)
    }

    private fun drawChart(xLabels: ArrayList<String>, lines: ArrayList<Line>, numberOfPoints: Int, maxValue: Int) {
        //图表属性设置
        chartView.isInteractive = true//设置图表是可以交互的（拖拽，缩放等效果的前提）
        chartView.zoomType = ZoomType.HORIZONTAL//设置缩放方向
        chartView.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL)

        //坐标轴设置
        val axisXY = AxisXY()
        val axisX = axisXY.axisX//x轴
        val axisY = axisXY.axisY//y轴
        axisXY.setAxisLabels(xLabels)

        axisX.typeface = Typeface.MONOSPACE
        axisY.typeface = Typeface.MONOSPACE
        axisX.textColor = 0xFF00897B.toInt()
        axisY.textColor = 0xFF00897B.toInt()
        axisY.setHasLines(true)
        axisX.maxLabelChars = 5
        axisX.name = axisXname
        axisX.setHasTiltedLabels(true)
        axisY.name = ""

        //填充数据
        val data = LineChartData()
        data.axisXBottom = axisX
        data.axisYLeft = axisY
        data.lines = lines  //设置图表折线
        data.baseValue = Float.NEGATIVE_INFINITY
        chartView.lineChartData = data

        //设置X、Y轴范围
        val maxViewPoint = Viewport(chartView.maximumViewport)
        val v = Viewport()
        v.bottom = 0f
        v.top = maxValue.toFloat() + 0.1f
        v.left = 0f
        v.right = 10f
        maxViewPoint.top = maxValue.toFloat() + 0.1f
        maxViewPoint.bottom = v.bottom - 0.1f
        maxViewPoint.right = numberOfPoints.toFloat() + 0.1f
        chartView.maximumViewport = maxViewPoint
        chartView.setCurrentViewportWithAnimation(v)

        progressBar.visibility = View.GONE
    }

    //XY轴设置
    inner class AxisXY() {
        val axisX = Axis()
        val axisY = Axis()

        fun setAxisValues(
            xValues: ArrayList<AxisValue>? = null,
            yValues: ArrayList<AxisValue>? = null
        ) {
            if (xValues != null) {
                axisX.values = xValues
            }
            if (yValues != null) {
                axisY.values = yValues
            }
        }

        fun setAxisLabels(xLabels: ArrayList<String>? = null, yLabels: ArrayList<String>? = null) {
            val axisXValues = ArrayList<AxisValue>()
            val axisYValues = ArrayList<AxisValue>()
            if (xLabels != null) {
                val xlabels_num = xLabels.size
                for (i in 0 until xlabels_num) {
                    axisXValues.add(AxisValue(i.toFloat()).setLabel(xLabels[i]))
                }
                axisX.values = axisXValues
            }
            if (yLabels != null) {
                val ylabels_num = yLabels.size
                for (i in 0 until ylabels_num) {
                    axisYValues.add(AxisValue(i.toFloat()).setLabel(yLabels[i]))
                }
                axisY.values = axisYValues
            }
        }
    }

    private fun setViewPort(
        cTop: Float = 0f,
        cBotton: Float = 0f,
        cLeft: Float = 0f,
        cRight: Float = 0f,
        maxTop: Float = 0f,
        maxButton: Float = 0f,
        maxLeft: Float = 0f,
        maxRight: Float = 0f
    ) {
        val viewport = Viewport()
        viewport.apply {
            top = cTop
            bottom = cBotton
            left = cLeft
            right = cRight
        }
        chartView.setCurrentViewportWithAnimation(viewport)

        val maxViewport = Viewport(chartView.maximumViewport)
        maxViewport.apply {
            top = maxTop
            bottom = maxButton
            right = maxRight
            left = maxLeft
        }
        chartView.maximumViewport = maxViewport

    }

}