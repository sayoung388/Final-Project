package com.example.syoung.final_project;

import java.util.Date;

/**
 * Created by syoung on 12/10/15.
 */
public class BillModel {
    private String billerName;
    private double billAmount;
    private String billDueDateString;


    public String getBillDueDateString() {
        return billDueDateString;
    }

    public int getBillDueDateInteger() {
        return Integer.parseInt(billDueDateString);
    }

    public void setBillDueDateString(String billDueDateString) {
        this.billDueDateString = billDueDateString;
    }

    public String getBillerName() {
        return billerName;
    }

    public void setBillerName(String billerName) {
        this.billerName = billerName;
    }

    public double getBillAmount() {
        return billAmount;
    }

    public String getBillAmountString() {
        return Double.toString(billAmount);
    }

    public void setBillAmount(double billAmount) {
        this.billAmount = billAmount;
    }


}
