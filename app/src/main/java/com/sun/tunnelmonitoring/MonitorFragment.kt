package com.sun.tunnelmonitoring


import android.annotation.SuppressLint
import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sun.tunnelmonitoring.tree.TreeFragment
import kotlinx.android.synthetic.main.fragment_monitor.*
import lecho.lib.hellocharts.gesture.ContainerScrollType
import lecho.lib.hellocharts.gesture.ZoomType
import lecho.lib.hellocharts.model.*
import java.security.SecureRandom


class MonitorFragment : Fragment() {
    private val maxNumberOfLines = 4
    private val numberOfPoints = 20
    private var randomNumbersTab = Array(maxNumberOfLines) { FloatArray(numberOfPoints) }

    init {
        //产生随机数据
        generateValues()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_monitor, container, false)
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //产生随机数据
        //generateValues()
        //绘制折线图
        drawChart()
        bt_baseinform.setOnClickListener {
            activity!!.supportFragmentManager
                .beginTransaction().add(R.id.activity_fragment, TreeFragment.newInstance())
                .addToBackStack(null)
                .commit()
        }

        bt_live_measure.setOnClickListener {
            activity!!.supportFragmentManager
                    .beginTransaction().add(R.id.activity_fragment, LiveMeasureFragment.newInstance())
                    .addToBackStack(null)
                    .commit()
        }

    }

    //生成随机值
    private fun generateValues() {
        for (i in 0 until maxNumberOfLines) {
            for (j in 0 until numberOfPoints) {
                randomNumbersTab[i][j] = SecureRandom().nextInt(90).toFloat()
            }
        }
    }

    private fun drawChart() {
        val lines=ArrayList<Line>()

        val colors=ArrayList<Int>()
        colors.add(0xFF2196F3.toInt())
        colors.add(0xFF66BB6A.toInt())
        colors.add(0xFF673AB7.toInt())
        colors.add(0xFFFFEB3B.toInt())

        for (i in 0 until maxNumberOfLines){
            val line = Line()
            val values=ArrayList<PointValue>()
            for (j in 0 until numberOfPoints){
                values.add(PointValue(j.toFloat(),randomNumbersTab[i][j]))
            }
            line.values=values
            line.color = colors[i]
            line.isCubic = true
            line.isFilled = true
            line.setHasPoints(false)
            lines.add(line)
        }

        //图表属性设置
        mChartView.isInteractive = true//设置图表是可以交互的（拖拽，缩放等效果的前提）
        mChartView.zoomType = ZoomType.HORIZONTAL//设置缩放方向
        mChartView.setContainerScrollEnabled(true,ContainerScrollType.HORIZONTAL)

        //坐标轴设置
        val axisXY=AxisXY()
        val axisX = axisXY.axisX//x轴
        val axisY = axisXY.axisY//y轴
        val xLabels=ArrayList<String>()
        xLabels.add("10:00")
        xLabels.add("10:20")
        xLabels.add("10:40")
        xLabels.add("11:00")
        xLabels.add("11:20")
        xLabels.add("11:40")
        xLabels.add("12:00")
        xLabels.add("12:20")
        xLabels.add("12:40")
        xLabels.add("13:00")
        xLabels.add("13:20")
        xLabels.add("13:40")
        xLabels.add("14:00")
        xLabels.add("14:20")
        xLabels.add("14:40")
        xLabels.add("15:00")
        xLabels.add("15:20")
        xLabels.add("15:40")
        xLabels.add("16:00")
        xLabels.add("16:20")
        axisXY.setAxisLabels(xLabels)

        axisX.typeface= Typeface.MONOSPACE
        axisY.typeface=Typeface.MONOSPACE
        axisX.textColor=0xFF00897B.toInt()
        axisY.textColor=0xFF00897B.toInt()
        axisY.setHasLines(true)
        axisX.maxLabelChars=5
        axisX.name="时间"
        axisX.setHasTiltedLabels(true)
        axisY.name="温度℃"

        //图标数据设置
        val data = LineChartData()
        data.axisXBottom = axisX
        data.axisYLeft = axisY
        data.lines = lines  //设置图表折线
        data.baseValue= Float.NEGATIVE_INFINITY
        mChartView.lineChartData = data//给图表填充数据


        //设置X、Y轴范围
        val maxViewPoint = Viewport(mChartView.maximumViewport)
        val v=Viewport()
        v.bottom = 0f
        v.top = 100f
        v.left=0f
        v.right=10f
        maxViewPoint.top=100f
        maxViewPoint.right=numberOfPoints.toFloat()+0.1f
        mChartView.maximumViewport = maxViewPoint
        mChartView.setCurrentViewportWithAnimation(v)
    }

    //XY轴设置
    inner class AxisXY(){
        val axisX = Axis()
        val axisY = Axis()

        fun setAxisValues(xValues:ArrayList<AxisValue>?=null,yValues:ArrayList<AxisValue>?=null){
            if(xValues!=null){
                axisX.values=xValues
            }
            if(yValues!=null){
                axisY.values=yValues
            }
        }
        fun setAxisLabels(xLabels:ArrayList<String>?=null,yLabels:ArrayList<String>?=null){
            val axisXValues= ArrayList<AxisValue>()
            val axisYValues= ArrayList<AxisValue>()
            if(xLabels!=null) {
                val xlabels_num=xLabels.size
                for (i in 0 until  xlabels_num) {
                      axisXValues.add(AxisValue(i.toFloat()).setLabel(xLabels[i]))
                }
                axisX.values=axisXValues
            }
            if(yLabels!=null) {
                val ylabels_num=yLabels.size
                for (i in 0 until ylabels_num) {
                    axisYValues.add(AxisValue(i.toFloat()).setLabel(yLabels[i]))
                }
                axisY.values=axisYValues
            }
        }
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var instance:MonitorFragment?=null
            get() {
                if(field==null){
                    field= MonitorFragment()
                }
                return field
            }
        @Synchronized
        fun get():MonitorFragment{
            return instance!!
        }
    }
}