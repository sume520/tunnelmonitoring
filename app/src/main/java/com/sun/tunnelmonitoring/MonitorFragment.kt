package com.sun.tunnelmonitoring


import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_monitor.*
import lecho.lib.hellocharts.model.Line
import lecho.lib.hellocharts.model.PointValue
import lecho.lib.hellocharts.model.LineChartData
import lecho.lib.hellocharts.gesture.ZoomType
import lecho.lib.hellocharts.model.Axis


class MonitorFragment : Fragment() {
    private var hasLabelForSelected=false
    private var hasLabels=false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_monitor, container, false)
        return view
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        drawChart()
    }
    private fun drawChart() {
        var lines = ArrayList<Line>()
        var values = ArrayList<PointValue>();//折线上的点
        values.add(PointValue(0F, 2f))
        values.add(PointValue(1F, 4f))
        values.add(PointValue(2F, 3f))
        values.add(PointValue(3F, 4f))
        values.add(PointValue(4F, 7f))
        values.add(PointValue(5F, 7f))
        values.add(PointValue(6F, 3f))
        values.add(PointValue(7F, 2f))

        val line = Line(values).setColor(0xFF2196F3.toInt())//声明线并设置颜色
        line.setCubic(false)//设置是平滑的还是直的
        lines.add(line)

        mChartView.setInteractive(true)//设置图表是可以交互的（拖拽，缩放等效果的前提）
        mChartView.setZoomType(ZoomType.HORIZONTAL_AND_VERTICAL)//设置缩放方向
        val data = LineChartData()
        val axisX = Axis()//x轴
        val axisY = Axis()//y轴
        axisX.lineColor=0xFF004D40.toInt()
        axisY.lineColor=0xFF004D40.toInt()
        axisX.typeface= Typeface.MONOSPACE
        data.axisXBottom = axisX
        data.axisYLeft = axisY
        data.lines = lines
        line.isFilled = true
        toggleLabelForSelected()
        mChartView.lineChartData = data//给图表设置数据
    }


    companion object {
        @JvmStatic
        fun newInstance() =
            MonitorFragment()
    }
}