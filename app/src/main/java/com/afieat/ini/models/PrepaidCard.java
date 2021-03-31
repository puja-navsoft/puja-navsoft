package com.afieat.ini.models;

/**
 * Created by amartya on 07/07/16 with love.
 */
public class PrepaidCard {
    private String cardNo;
    private String balance;
    private String rechargeDate;

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getRechargeDate() {
        return rechargeDate;
    }

    public void setRechargeDate(String rechargeDate) {
        this.rechargeDate = rechargeDate;
    }
}
