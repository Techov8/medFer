package com.techov8.medfer;

import java.util.ArrayList;

public class ItemListModel {
    private String itemIcon;
    private String itemTitle;
    private String itemAddress;
    private String itemDiscount;
    private String itemID;
    private String homeTitleID;
    private ArrayList<String> tags;
    private String experience, timing, location;
    private String details, sittingWhere;
    private String subCategory;

    public ItemListModel(String itemIcon, String itemTitle, String itemAddress, String itemDiscount, String itemID, String homeTitleID, String experience, String timing, String location, String details, String sittingWhere,String subCategory) {
        this.itemIcon = itemIcon;
        this.itemTitle = itemTitle;
        this.itemAddress = itemAddress;
        this.itemDiscount = itemDiscount;
        this.itemID = itemID;
        this.homeTitleID = homeTitleID;
        this.experience = experience;
        this.timing = timing;
        this.location = location;
        this.details = details;
        this.sittingWhere = sittingWhere;
        this.subCategory=subCategory;
    }

    public String getItemIcon() {
        return itemIcon;
    }

    public String getItemTitle() {
        return itemTitle;
    }

    public String getItemAddress() {
        return itemAddress;
    }

    public String getItemDiscount() {
        return itemDiscount;
    }

    public String getItemID() {
        return itemID;
    }

    public String getHomeTitleID() {
        return homeTitleID;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public String getExperience() {
        return experience;
    }

    public String getTiming() {
        return timing;
    }


    public String getLocation() {
        return location;
    }

    public String getDetails() {
        return details;
    }

    public String getSittingWhere() {
        return sittingWhere;
    }

    public String getSubCategory() {
        return subCategory;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }
}