package com.sun.tunnelmonitoring.projectTree;

/**
 * Created by ZBL on 2018/12/12.
 */

public class TreeInfo {
    private int id;
    private int groupId;//组ID
    private String name;

    public TreeInfo(int id, int groupId, String name) {
        this.id = id;
        this.groupId = groupId;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

