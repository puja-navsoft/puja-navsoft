package com.afieat.ini.models;

public class MyPointsModel {

    private String order_id;
    private String order_no;
    private String merchant_id;
    private String amount;
    private String points;
    private String date;
    private String status;
    private String redeem_points;

    public MyPointsModel(String order_id,
                         String order_no,
                         String merchant_id,
                         String amount,
                         String points,
                         String date,
                         String status,
                         String redeem_points) {
        this.order_id = order_id;
        this.order_no = order_no;
        this.merchant_id = merchant_id;
        this.amount = amount;
        this.points = points;
        this.date = date;
        this.status = status;
        this.redeem_points = redeem_points;
    }

    public String getRedeem_points() {
        return redeem_points;
    }

    public void setRedeem_points(String redeem_points) {
        this.redeem_points = redeem_points;
    }

    public String getOrder_no() {
        return order_no;
    }

    public void setOrder_no(String order_no) {
        this.order_no = order_no;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getMerchant_id() {
        return merchant_id;
    }

    public void setMerchant_id(String merchant_id) {
        this.merchant_id = merchant_id;
    }
}
