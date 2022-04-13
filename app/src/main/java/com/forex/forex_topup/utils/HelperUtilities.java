package com.forex.forex_topup.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class HelperUtilities {
    public JSONObject serverResponse;
    public RequestQueue queue;
    public Context context;
    private Activity activity;
    private String apiResponse;
    private String apiResponseCode;
    public SweetAlertDialog pDialog;
    public SweetAlertDialog alertDialog;
    //private PrefManager prefManager;
    private String formattedMobileNumber;

    public HelperUtilities(Context context){
        this.context=context;
        this.serverResponse = null;
        this.queue = Volley.newRequestQueue(context);
        this.activity = new Activity();
        this.serverResponse = null;
        this.apiResponse = "";
        //this.prefManager = new PrefManager(context);
    }

    public double roundTruncate(double value){
        DecimalFormat df = new DecimalFormat("0.00");
        String roundedTo2dp = df.format(value);
        return Double.parseDouble(roundedTo2dp);
    }

    public  double round (double value, int precision) {
        int scale = (int) Math.pow(10, precision);
        return (double) Math.round(value * scale) / scale;
    }


    public boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    public String formatNumber(String mobileNumber){
        this.formattedMobileNumber = mobileNumber;
        int stringLength = mobileNumber.length();
        if(mobileNumber.startsWith("+")){
            this.formattedMobileNumber = mobileNumber.substring(1,stringLength);
        }


        if(this.formattedMobileNumber.length() < 10){
            return formattedMobileNumber;
        }

        char first3Char = this.formattedMobileNumber.charAt(2);
        if(String.valueOf(first3Char).equals("254") && this.formattedMobileNumber.length() == 12){ //number is valid
            return this.formattedMobileNumber;
        }

        if(this.formattedMobileNumber.length() == 9){
            this.formattedMobileNumber = "254"+this.formattedMobileNumber;
            return formattedMobileNumber;
        }
        if(String.valueOf(first3Char).equals("254") && this.formattedMobileNumber.length() == 9){
            this.formattedMobileNumber = "254"+this.formattedMobileNumber;
            return formattedMobileNumber;
        }
        if(String.valueOf(this.formattedMobileNumber.charAt(0)).equals("0") && this.formattedMobileNumber.length() == 10){
            //remove first digit-0 , and append 254
            this.formattedMobileNumber = "254"+this.formattedMobileNumber.substring(1, stringLength);
            return formattedMobileNumber;
        }
        return formattedMobileNumber;
    }

    /**
     *
     * @param postParam
     * @param callback
     * @param apiEndpoint
     */
    public void volleyHttpPostRequestV2(final Map<String, Object> postParam, final ResponseCallback callback, final String apiEndpoint){

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                apiEndpoint, new JSONObject(postParam),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("api response", response.toString()+" payload:"+postParam.toString());
                        try {
//                            prefManager.setReceivedStatusCode(response.getString("status"));
//                            prefManager.setReceivedStatusMessage(response.getString("message"));
                            apiResponse = response.getString("message");
                        } catch (JSONException e) {
                            e.printStackTrace();
                            apiResponse = e.getMessage();
                        }
                        callback.onResponse(response);
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Tag", "Error: " + error.getMessage()+" payload:"+postParam.toString());
                NetworkResponse networkResponse = error.networkResponse;
                if (networkResponse != null && networkResponse.data != null) {
                    String jsonError = new String(networkResponse.data);
                    VolleyLog.d(jsonError);

                    try {

                        serverResponse = new JSONObject(jsonError);
                        callback.onErrorResponse(serverResponse);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    // Print Error!
                }

            }
        }) {

            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                int mStatusCode = response.statusCode;
                VolleyLog.d("statusCode", ""+mStatusCode);
                //tostMessage(prefManager.getReceivedStatusMessage());

                return super.parseNetworkResponse(response);
            }

            /**
             * Passing some request headers
             * */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                //headers.put("Authorization", "Bearer "+prefManager.getApiHeaderBearer());
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        jsonObjReq.setTag("Tag");
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Adding request to request queue
        queue.add(jsonObjReq);
    }

    public void volleyHttpGetRequestV2(Map<String, Object> postParam, final ResponseCallback callback, final String apiEndpoint){
        Log.d("api url to invoke :", Configs.apiUrl+apiEndpoint);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                apiEndpoint, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d("api response", response.toString());
                try {
//                    prefManager.setReceivedStatusCode(response.getString("status"));
//                    prefManager.setReceivedStatusMessage(response.getString("message"));
                    apiResponse = response.getString("statusDescription");
                } catch (JSONException e) {
                    e.printStackTrace();
                    apiResponse = e.getMessage();
                }
                callback.onResponse(response);
            }
        }, new Response.ErrorListener() {


            @Override
            public void onErrorResponse(VolleyError error) {

                Log.d("error api response", error.toString());

                NetworkResponse networkResponse = error.networkResponse;
                if (networkResponse != null && networkResponse.data != null) {
                    String jsonError = new String(networkResponse.data);
                    VolleyLog.d(jsonError);
                    try {
                        serverResponse = new JSONObject(jsonError);
                        callback.onErrorResponse(serverResponse);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        }) {

            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                int mStatusCode = response.statusCode;
                VolleyLog.d("statusCode", ""+mStatusCode);
                return super.parseNetworkResponse(response);
            }


            /**
             * Passing some request headers
             * */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                HashMap<String, String> headers = new HashMap<String, String>();
                //headers.put("Content-Type", "application/json; charset=utf-8");
                //headers.put("Authorization", "Bearer "+prefManager.getApiHeaderBearer());

                return headers;
            }
        };

        jsonObjReq.setTag("Tag");
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Adding request to request queue
        queue.add(jsonObjReq);
    }

    public void showErrorMessage(Context context ,String message){
        this.alertDialog = new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE);
        this.alertDialog.setTitleText("Oops...")
                .setContentText(message)
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                    }
                })
                .show();
    }

    public void showLoadingBar(Context context , String loadingMessage){
        this.pDialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
        this.pDialog.getProgressHelper().setBarColor(Color.parseColor("#4d8ef1"));
        this.pDialog.setTitleText(loadingMessage);
        this.pDialog.setCancelable(false);
        this.pDialog.show();
    }

    public void hideLoadingBar()
    {
        this.pDialog.cancel();
    }

    public String getPmOrAM(int hour){
        String timeOfday = "AM";
        if(hour >= 12 ){
            timeOfday = "PM";
        }
        return timeOfday;
    }


    public String getSelectedMonthInString(int month){
        String monthInString = null;
        if(month == 1){
            monthInString = "Jan";
        }
        if(month == 2){
            monthInString = "Feb";
        }
        if(month == 3){
            monthInString = "March";
        }
        if(month == 4){
            monthInString = "April";
        }
        if(month == 5){
            monthInString = "May";
        }
        if(month == 6){
            monthInString = "June";
        }
        if(month == 7){
            monthInString = "July";
        }
        if(month == 8){
            monthInString = "Augt";
        }
        if(month == 9){
            monthInString = "Sept";
        }
        if(month == 10){
            monthInString = "Oct";
        }
        if(month == 11){
            monthInString = "Nov";
        }
        if(month == 12){
            monthInString = "Dec";
        }
        return monthInString;
    }


}
