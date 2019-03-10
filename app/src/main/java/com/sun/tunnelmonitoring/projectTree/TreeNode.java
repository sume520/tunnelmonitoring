package com.sun.tunnelmonitoring.projectTree;

import java.util.ArrayList;

/**
 * Created by ZBL on 2018/12/12.
 */

public class TreeNode {

    private TreeNode parent = null;//父节点
    private String name;//名称
    private boolean isLeaf = false;//是否为叶节点
    private boolean isExpanded = false;//该节点是否展开
    private ArrayList<TreeNode> childList = null;//子树
    private boolean isCheck = false;

    public TreeNode(TreeNode treeNode, String name, boolean isLeaf) {
        this.parent = treeNode;
        this.name = name;
        this.isLeaf = isLeaf;
    }

    public TreeNode getParent() {
        return parent;
    }

    public void setParent(TreeNode parent) {
        this.parent = parent;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isLeaf() {
        return isLeaf;
    }

    public void setLeaf(boolean leaf) {
        isLeaf = leaf;
    }

    public ArrayList<TreeNode> getChildList() {
        return childList;
    }

    public void setChildList(ArrayList<TreeNode> childList) {
        this.childList = childList;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    //得到当前节点所在的层数，根为0层
    public int getLevel() {
        return parent == null ? 0 : parent.getLevel() + 1;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }

    //添加子节点
    public void addChildNode(TreeNode node) {
        if (childList == null) {
            childList = new ArrayList<>();
        }
        childList.add(node);
    }

    //清空子节点
    public void clearChildren() {
        if (childList != null) {
            childList.clear();
        }
    }

}

