package com.example.syoung.final_project;

import java.util.Date;

/**
 * Created by syoung on 12/8/15.
 */
public class FoodStorageDataModel {
    private String itemName;
    private String itemQuantity;
    private String purchaseDate;
    private String expirationDate;
    private Date purchaseDateValue;
    private Date expirationDateValue;

    public Date getPurchaseDateValue() {
        return purchaseDateValue;
    }

    public void setPurchaseDateValue(Date purchaseDateValue) {
        this.purchaseDateValue = purchaseDateValue;
    }

    public Date getExpirationDateValue() {
        return expirationDateValue;
    }

    public void setExpirationDateValue(Date expirationDateValue) {
        this.expirationDateValue = expirationDateValue;
    }



    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemQuantity() {
        return itemQuantity;
    }

    public void setItemQuantity(String itemQuantity) {
        this.itemQuantity = itemQuantity;
    }

    public String getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(String purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }
}
