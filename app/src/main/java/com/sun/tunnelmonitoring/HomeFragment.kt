package com.sun.tunnelmonitoring

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.sun.tunnelmonitoring.db.manager.Temperature
import com.sun.tunnelmonitoring.tree.TreeFragment
import com.threshold.logger.PrettyLogger
import kotlinx.android.synthetic.main.fragment_home.*
import lecho.lib.hellocharts.gesture.ContainerScrollType
import lecho.lib.hellocharts.gesture.ZoomType
import lecho.lib.hellocharts.model.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.litepal.LitePal
import org.litepal.extension.findAll
import java.security.SecureRandom


class HomeFragment : Fragment(), PrettyLogger {
    private var maxNumberOfLines = 0
    private var numberOfPoints = 0
    private var randomNumbersTab = Array(maxNumberOfLines) { FloatArray(numberOfPoints) }
    private var sensorType:String?=null
    private var unitSignal:String?=null
    private var values:ArrayList<PointValue>?=null
    private var xLabels:ArrayList<String>?=null
    private var axisXname:String?=null
    private var axisYname:String?=null
    private val lines = ArrayList<Line>()

    init {
        //产生随机数据
        generateValues()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //注册Eventbus事件
        EventBus.getDefault().register(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_home, container, false)

        return view
    }

    companion object {
        var count=0

        @SuppressLint("StaticFieldLeak")
        private var instance: HomeFragment? = null
            get() {
                if (field == null) {
                    field = HomeFragment()
                }
                return field
            }

        @Synchronized
        fun get(): HomeFragment {
            return instance!!
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //绘制折线图
        //drawChart()
        selectSersor("温度计")
        bt_baseinform.setOnClickListener {
            activity!!.supportFragmentManager
                .beginTransaction().add(R.id.activity_fragment, TreeFragment.newInstance())
                .addToBackStack(null)
                .commit()
        }

        var adapter=ArrayAdapter.createFromResource(activity,
                R.array.sensor_array,android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spin_sensor.adapter=adapter
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    //在UI线程中处理EventBus事件
    fun onUIUpdateEvent(messageEvent: MessageEvent) {
    }


    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }

    //生成随机值
    private fun generateValues() {
        for (i in 0 until maxNumberOfLines) {
            for (j in 0 until numberOfPoints) {
                randomNumbersTab[i][j] = SecureRandom().nextInt(90).toFloat()
            }
        }
    }

    private fun selectSersor(sensor:String){

        val colors =
            arrayOf(0xFF2196F3.toInt(),0xFF66BB6A.toInt(),0xFF673AB7.toInt(),0xFFFFEB3B.toInt())

        values= ArrayList()

        when(sensor){
            "温度计"->{
                maxNumberOfLines=1
                //从数据库获取数据
                val temps = LitePal.findAll<Temperature>()
                numberOfPoints = temps.size
                //填充的数据
                values!!.clear()
                for (i in 0 until maxNumberOfLines) {
                    val line = Line()
                    val values = ArrayList<PointValue>()
                    for (j in 0 until numberOfPoints) {
                        values.add(PointValue(j.toFloat(), temps[j].temp.toFloat()))
                    }
                    line.values = values
                    line.color = colors[i]
                    line.isCubic = true
                    line.isFilled = true
                    line.setHasPoints(false)
                    lines.add(line)
                }
                //x轴标签
                xLabels= ArrayList()
                for (i in 0 until numberOfPoints) {
                    var str: String
                    if (temps[i].time == "12:00")
                        str = temps[i].date.substring(5)
                    else
                        str = temps[i].time
                    xLabels!!.add(str)
                }
                //XY坐标名称
                axisXname="时间"
                axisXname="温度℃"
            }
        }

        //绘制图表
        drawChart()
    }

    private fun drawChart() {

        //图表属性设置
        mChartView.isInteractive = true//设置图表是可以交互的（拖拽，缩放等效果的前提）
        mChartView.zoomType = ZoomType.HORIZONTAL//设置缩放方向
        mChartView.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL)

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
        axisY.name = axisYname

        //填充数据
        val data = LineChartData()
        data.axisXBottom = axisX
        data.axisYLeft = axisY
        data.lines = lines  //设置图表折线
        data.baseValue = Float.NEGATIVE_INFINITY
        mChartView.lineChartData = data


        //设置X、Y轴范围
        val maxViewPoint = Viewport(mChartView.maximumViewport)
        val v = Viewport()
        v.bottom = 0f
        v.top = 50f
        v.left = 0f
        v.right = 10f
        maxViewPoint.top = 50f
        maxViewPoint.bottom = v.bottom - 0.1f
        maxViewPoint.right = numberOfPoints.toFloat() + 0.1f
        mChartView.maximumViewport = maxViewPoint
        mChartView.setCurrentViewportWithAnimation(v)
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

}
