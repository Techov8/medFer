package com.techov8.medfer;

public class AppointmentDetailsModel {
    private String logo,name,category,time,timeStatus,paidStatus,date,bookingStatus,bookingId,amount;

    public AppointmentDetailsModel(String logo, String name, String category, String time, String timeStatus, String paidStatus, String date, String bookingStatus, String bookingId, String amount) {
        this.logo = logo;
        this.name = name;
        this.category = category;
        this.time = time;
        this.timeStatus = timeStatus;
        this.paidStatus = paidStatus;
        this.date = date;
        this.bookingStatus = bookingStatus;
        this.bookingId = bookingId;
        this.amount = amount;
    }

    public String getLogo() {
        return logo;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public String getTime() {
        return time;
    }

    public String getTimeStatus() {
        return timeStatus;
    }

    public String getPaidStatus() {
        return paidStatus;
    }

    public String getDate() {
        return date;
    }

    public String getBookingStatus() {
        return bookingStatus;
    }

    public String getBookingId() {
        return bookingId;
    }

    public String getAmount() {
        return amount;
    }
}
