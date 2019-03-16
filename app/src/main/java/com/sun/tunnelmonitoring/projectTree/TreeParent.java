package com.sun.tunnelmonitoring.projectTree;

/**
 * Created by ZBL on 2018/12/12.
 */

public class TreeParent {
    private int id;//对应节点的groupId
    private int parentId;//父类编号
    private String name;//名称

    /**
     * @param id
     * @param parentId
     * @param name
     */
    public TreeParent(int id, int parentId, String name) {
        this.id = id;
        this.parentId = parentId;
        this.name = name;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
