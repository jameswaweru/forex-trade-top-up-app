package com.forex.forex_topup.models;

public class Transactions {

    private String transactionID;
    private String transactionAmount;
    private String transactionText;
    private String transactionMsisdn;
    private String transactionIcon;
    private String transactionDate;
    private String transactionTime;
    private String transactionServiceName;
    private String transactionServiceCategoryID;
    private String transactionServiceCategoryName;
    private String transactionAccountNumber;
    private String transactionStatusID;



    public Transactions() {
    }

    public Transactions(String transactionID, String transactionAmount, String transactionText, String transactionMsisdn,
                        String transactionIcon, String transactionDate, String transactionTime, String transactionServiceName,
                        String transactionServiceCategoryID, String transactionServiceCategoryName,
                        String transactionAccountNumber, String transactionStatusID) {
        this.transactionID = transactionID;
        this.transactionAmount = transactionAmount;
        this.transactionText = transactionText;
        this.transactionMsisdn = transactionMsisdn;
        this.transactionIcon = transactionIcon;
        this.transactionDate = transactionDate;
        this.transactionTime = transactionTime;
        this.transactionServiceName = transactionServiceName;
        this.transactionServiceCategoryID = transactionServiceCategoryID;
        this.transactionServiceCategoryName = transactionServiceCategoryName;
        this.transactionAccountNumber = transactionAccountNumber;
        this.transactionStatusID = transactionStatusID;
    }

    public String getTransactionID() {
        return transactionID;
    }

    public void setTransactionID(String transactionID) {
        this.transactionID = transactionID;
    }

    public String getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(String transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public String getTransactionText() {
        return transactionText;
    }

    public void setTransactionText(String transactionText) {
        this.transactionText = transactionText;
    }

    public String getTransactionMsisdn() {
        return transactionMsisdn;
    }

    public void setTransactionMsisdn(String transactionMsisdn) {
        this.transactionMsisdn = transactionMsisdn;
    }

    public String getTransactionIcon() {
        return transactionIcon;
    }

    public void setTransactionIcon(String transactionIcon) {
        this.transactionIcon = transactionIcon;
    }


    public String getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(String transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getTransactionTime() {
        return transactionTime;
    }

    public void setTransactionTime(String transactionTime) {
        this.transactionTime = transactionTime;
    }

    public String getTransactionServiceName() {
        return transactionServiceName;
    }

    public void setTransactionServiceName(String transactionServiceName) {
        this.transactionServiceName = transactionServiceName;
    }

    public String getTransactionServiceCategoryID() {
        return transactionServiceCategoryID;
    }

    public void setTransactionServiceCategoryID(String transactionServiceCategoryID) {
        this.transactionServiceCategoryID = transactionServiceCategoryID;
    }

    public String getTransactionServiceCategoryName() {
        return transactionServiceCategoryName;
    }

    public void setTransactionServiceCategoryName(String transactionServiceCategoryName) {
        this.transactionServiceCategoryName = transactionServiceCategoryName;
    }

    public String getTransactionAccountNumber() {
        return transactionAccountNumber;
    }

    public void setTransactionAccountNumber(String transactionAccountNumber) {
        this.transactionAccountNumber = transactionAccountNumber;
    }

    public String getTransactionStatusID() {
        return transactionStatusID;
    }

    public void setTransactionStatusID(String transactionStatusID) {
        this.transactionStatusID = transactionStatusID;
    }
}
