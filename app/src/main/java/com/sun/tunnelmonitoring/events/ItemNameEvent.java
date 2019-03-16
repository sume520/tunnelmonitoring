package com.sun.tunnelmonitoring.events;

/**
 * Created by ZBL on 2019/1/18.
 */

public class ItemNameEvent {
    private String ItemName;

    public ItemNameEvent(String ItemName) {
        this.ItemName = ItemName;
    }

    public String getItemName() {
        return ItemName;
    }

    public void setItemName(String ItemName) {
        this.ItemName = ItemName;
    }
}
