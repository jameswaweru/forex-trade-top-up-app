package com.forex.forex_topup.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefManager {

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;

    // shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "binary";
    String MSISDN_KEY = "MSISDN";
    String USER_ID = "userId";
    String USD_CONVERSION_RATE = "usedConversionRate";
    String USD_DEPOSIT_RATE = "usdDepositRate";
    String USD_WITHDRAWAL_RATE = "usdWithdrawalRate";
    String DEPOSIT_LIMIT = "depositLimit";
    String DEPOSIT_LOWER_LIMIT = "depositLowerLimit";

    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";
    private static final String IS_RESETING_PIN= "isResetingPin";

    public PrefManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setDepositLimit(String depositLimit){
        editor.putString(DEPOSIT_LIMIT,depositLimit);
        editor.commit();
    }
    public String getDepositLimit(){
        return pref.getString(DEPOSIT_LIMIT, "0");
    }

    public void setDepositLowerLimit(String depositLowerLimit){
        editor.putString(DEPOSIT_LOWER_LIMIT,depositLowerLimit);
        editor.commit();
    }
    public String getDepositLowerLimit(){
        return pref.getString(DEPOSIT_LOWER_LIMIT, "0");
    }


    public void setMSISDN(String msisdn){
        editor.putString(MSISDN_KEY,msisdn);
        editor.commit();
    }
    public String getMSISDN(){
        return pref.getString(MSISDN_KEY, "");
    }

    public void setUsdDepositRate(String usdDepositRate){
        editor.putString(USD_DEPOSIT_RATE,usdDepositRate);
        editor.commit();
    }
    public String getUsdDepositRate(){
        return pref.getString(USD_DEPOSIT_RATE, "1");
    }


    public void setUsdWithdrawRate(String usdWithdrawRate){
        editor.putString(USD_WITHDRAWAL_RATE,usdWithdrawRate);
        editor.commit();
    }
    public String getUsdWithdrawRate(){
        return pref.getString(USD_WITHDRAWAL_RATE, "1");
    }


    public void setUsdConversionRate(String usdConversionRate){
        editor.putString(USD_CONVERSION_RATE,usdConversionRate);
        editor.commit();
    }
    public String getUsdConversionRate(){
        return pref.getString(USD_CONVERSION_RATE, "1");
    }



    public void setUserId(String userId){
        editor.putString(USER_ID,userId);
        editor.commit();
    }
    public String getUserId(){
        return pref.getString(USER_ID, "0");
    }


    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        editor.commit();
    }
    public boolean isFirstTimeLaunch() {
        return pref.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }

    public void setIsResetingPin(boolean isResetingPin) {
        editor.putBoolean(IS_RESETING_PIN, isResetingPin);
        editor.commit();
    }
    public boolean isResetingPin() {
        return pref.getBoolean(IS_RESETING_PIN, false);
    }


}
