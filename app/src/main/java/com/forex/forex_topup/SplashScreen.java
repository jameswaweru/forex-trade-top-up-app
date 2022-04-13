package com.forex.forex_topup;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.forex.forex_topup.onboarding.Login;
import com.forex.forex_topup.onboarding.Register;
import com.forex.forex_topup.onboarding.ResetPassword;
import com.forex.forex_topup.utils.Configs;
import com.forex.forex_topup.utils.HelperUtilities;
import com.forex.forex_topup.utils.PrefManager;
import com.forex.forex_topup.utils.ResponseCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class SplashScreen extends AppCompatActivity {

    PrefManager prefManager;
    ImageView displayNoInternet, displayLogo;
    TextView noInternetText;
    private boolean isStillProcessing = false;
    HelperUtilities helperUtilities;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        helperUtilities = new HelperUtilities(getApplicationContext());
        displayNoInternet = findViewById(R.id.no_internet_logo);
        displayLogo = findViewById(R.id.digischool_logo);
        noInternetText = findViewById(R.id.no_internet_text);
        displayNoInternet.setVisibility(View.GONE);
        displayLogo.setVisibility(View.VISIBLE);
        noInternetText.setVisibility(View.GONE);

        prefManager = new PrefManager(getApplicationContext());

        if(!isConnected()){

            Toast.makeText(getApplicationContext(),"No internet",Toast.LENGTH_LONG).show();

            displayNoInternet.setVisibility(View.VISIBLE);
            displayLogo.setVisibility(View.GONE);
            noInternetText.setVisibility(View.VISIBLE);


        }else{


            if(prefManager.isResetingPin()){
                startActivity(new Intent(getApplicationContext() , ResetPassword.class));
                finish();
            } else if(prefManager.isFirstTimeLaunch()){
                startActivity(new Intent(getApplicationContext() , Login.class));
                finish();
            }else {
                fetchUserDetails();

            }


        }
    }

    public void fetchUserDetails(){
        isStillProcessing = true;
        //helperUtilities.showLoadingBar(getApplicationContext() , "Please wait.");

        final Map<String, Object> postParam = new HashMap<String, Object>();


        helperUtilities.volleyHttpGetRequestV2(postParam, new ResponseCallback() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onResponse(JSONObject response) {

                //helperUtilities.hideLoadingBar();
                isStillProcessing = false;

                try {
                    //String status =
                    int status = Integer.parseInt(response.getString("statusCode"));
                    if(status == Configs.sucessStatusCode){
                        JSONObject data = response.getJSONObject("data");
                        JSONObject tradeSettings = data.getJSONObject("tradeSettings");
                        //prefManager.setUsdConversionRate(tradeSettings.getString("usdConversionAmount"));

                        prefManager.setUsdConversionRate(tradeSettings.getString("usdConversionAmount"));
                        prefManager.setUsdDepositRate(tradeSettings.getString("depositConversionRate"));
                        prefManager.setUsdWithdrawRate(tradeSettings.getString("withdrawConversionRate"));
                        prefManager.setDepositLimit(tradeSettings.getString("depositLimit"));
                        prefManager.setDepositLowerLimit(tradeSettings.getString("depositLowerLimit"));

                    }

                    startActivity(new Intent(getApplicationContext() , MainActivity.class));
                    finish();

                    //loadingSpinKit.setVisibility(View.GONE);
                } catch (JSONException e) {
                    e.printStackTrace();

                }
            }

            @Override
            public void onErrorResponse(JSONObject response) {
                isStillProcessing = false;
                //helperUtilities.hideLoadingBar();
                //((ListBillPaymentsServices)getActivity()).hideLoadingLayout();

                Log.d("action:", "error response from api..");

                if(response.isNull("statusDescription")) {
                    helperUtilities.showErrorMessage(getApplicationContext(), response.toString());
                }else{
                    try {
                        //showErrorMessage(response.getString("message"));

                        Log.d("action:", "error response from api..:"+response.getString("statusDescription"));

                        Log.d("action:", "error response from api..:"+response.getString("statusDescription"));
                        if(response.getInt("statusCode") == Configs.outdatedVersion){
                            SweetAlertDialog pDialog = new SweetAlertDialog(SplashScreen.this, SweetAlertDialog.ERROR_TYPE);
//                                pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                            pDialog.setTitleText("Update App");
                            pDialog.setContentText(response.getString("statusDescription"));
                            pDialog.setCancelable(false);
                            pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    //sDialog.dismissWithAnimation();

                                    //https://play.google.com/store/apps/details?id=com.digischool.digischool
                                    Intent i = new Intent(Intent.ACTION_VIEW);
                                    i.setData(Uri.parse("https://binarycashmpesa.com/apps/binarycashmpesa-android.apk"));
                                    startActivity(i);

                                }
                            });
                            pDialog.show();
                        }else {
                            helperUtilities.showErrorMessage(getApplicationContext(), response.getString("statusDescription"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

        },Configs.apiUrl+"users/getUserDetails/"+prefManager.getUserId()); //+prefManager.getUserId()
    }

    public boolean isConnected() {
        boolean connected = false;
        try {
            ConnectivityManager cm = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo nInfo = cm.getActiveNetworkInfo();
            connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();
            return connected;
        } catch (Exception e) {
            Log.e("Connectivity Exception", e.getMessage());
        }
        return connected;
    }
}