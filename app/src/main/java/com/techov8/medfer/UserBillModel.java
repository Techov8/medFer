package com.techov8.medfer;

public class UserBillModel {
    private String billImage;
    private String billStatus;
    private String discountPercentage;
    private String totalAmount;
    private String itemName,itemSubName;
    private String status;


    public UserBillModel(String billImage, String billStatus, String discountPercentage, String totalAmount, String itemName, String itemSubName, String status) {
        this.billImage = billImage;
        this.billStatus = billStatus;
        this.discountPercentage = discountPercentage;
        this.totalAmount = totalAmount;
        this.itemName = itemName;
        this.itemSubName = itemSubName;
        this.status = status;
    }

    public String getBillImage() {
        return billImage;
    }

    public String getBillStatus() {
        return billStatus;
    }

    public String getDiscountPercentage() {
        return discountPercentage;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public String getItemName() {
        return itemName;
    }

    public String getItemSubName() {
        return itemSubName;
    }

    public String getStatus() {
        return status;
    }
}
