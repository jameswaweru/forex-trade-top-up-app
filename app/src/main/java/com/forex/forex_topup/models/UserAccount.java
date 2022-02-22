package com.forex.forex_topup.models;

public class UserAccount {

    private int userAccountId;
    private String accountNumber;

    public UserAccount(int userAccountId, String accountNumber) {
        this.userAccountId = userAccountId;
        this.accountNumber = accountNumber;
    }

    public int getUserAccountId() {
        return userAccountId;
    }

    public void setUserAccountId(int userAccountId) {
        this.userAccountId = userAccountId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }
}
