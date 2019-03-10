package com.sun.tunnelmonitoring.projectTree;

/**
 * Created by ZBL on 2019/1/14.
 */

public class Project_tree {
    private String circuit;
    private String section;
    private String work_order;
    private String work_point;
    private String fracture_surface;
    private String measure_point;

    //线路
    public String getCircuit() {
        return circuit;
    }

    public void setCircuit(String circuit) {
        this.circuit = circuit;
    }

    //标段
    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    //工区
    public String getWork_order() {
        return work_order;
    }

    public void setWork_order(String work_order) {
        this.work_order = work_order;
    }

    //工点
    public String getWork_point() {
        return work_point;
    }

    public void setWork_point(String work_point) {
        this.work_point = work_point;
    }

    //断面
    public String getFracture_surface() {
        return fracture_surface;
    }

    public void setFracture_surface(String fracture_surface) {
        this.fracture_surface = fracture_surface;
    }

    //测点
    public String getMeasure_point() {
        return measure_point;
    }

    public void setMeasure_point(String measure_point) {
        this.measure_point = measure_point;
    }

}
