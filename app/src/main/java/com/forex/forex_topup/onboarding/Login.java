package com.forex.forex_topup.onboarding;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.forex.forex_topup.MainActivity;
import com.forex.forex_topup.R;
import com.forex.forex_topup.SplashScreen;
import com.forex.forex_topup.utils.Configs;
import com.forex.forex_topup.utils.HelperUtilities;
import com.forex.forex_topup.utils.PrefManager;
import com.forex.forex_topup.utils.ResponseCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class Login extends AppCompatActivity {

    ImageView backButton;
    TextView toolBarText;
    Button btnSubmit;
    EditText iPhoneNum, iPassword;

    private boolean isStillProcessing = false;
    HelperUtilities helperUtilities;
    PrefManager prefManager;
    TextView openRegisterPage , openResetPin;
    private ConstraintLayout constraintLayout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        constraintLayout = findViewById(R.id.rootview);
        openResetPin = findViewById(R.id.forgot_password_link);
        openRegisterPage = findViewById(R.id.register_link);
        helperUtilities = new HelperUtilities(getApplicationContext());
        prefManager = new PrefManager(getApplicationContext());
        iPhoneNum = findViewById(R.id.input_msisdn);
        iPassword = findViewById(R.id.input_password);

        btnSubmit = findViewById(R.id.submitButton);
        toolBarText = findViewById(R.id.toolbar_text);
        backButton = findViewById(R.id.back_btn);
        toolBarText.setText("Login");


        iPhoneNum.setText(prefManager.getMSISDN());
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        constraintLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                constraintLayout.getWindowVisibleDisplayFrame(r);
                int screenHeight = constraintLayout.getRootView().getHeight();
                int keypadHeight = screenHeight - r.bottom;
                if (keypadHeight > screenHeight * 0.15) { //open
                    //btnRequestCode.setVisibility(View.GONE);
//                    btnFirstButtonsLayout.setVisibility(View.GONE);
//                    btnSecondButtonsLayout.setVisibility(View.VISIBLE);
                } else {
//                    btnFirstButtonsLayout.setVisibility(View.VISIBLE);
//                    btnSecondButtonsLayout.setVisibility(View.GONE);

                }
            }
        });
        openResetPin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), GenerateSmsOtp.class));
            }
        });

        openRegisterPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Register.class));
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(isStillProcessing){
                    Toast.makeText(getApplicationContext() , "Please wait",Toast.LENGTH_LONG).show();
                    return;
                }

                if(TextUtils.isEmpty(iPhoneNum.getText().toString())){
                    Toast.makeText(getApplicationContext() , "Enter phone number",Toast.LENGTH_LONG).show();
                    return;
                }

                if(TextUtils.isEmpty(iPassword.getText().toString())){
                    Toast.makeText(getApplicationContext() , "Enter password",Toast.LENGTH_LONG).show();
                    return;
                }

                loginUser();

            }
        });

    }



    public void loginUser(){
        isStillProcessing = true;
        helperUtilities.showLoadingBar(Login.this , "Logging.");

        final Map<String, Object> postParam = new HashMap<String, Object>();
        postParam.put("phoneNumber" , iPhoneNum.getText().toString());
        postParam.put("password" , iPassword.getText().toString());

        helperUtilities.volleyHttpPostRequestV2(postParam, new ResponseCallback() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onResponse(JSONObject response) {

                helperUtilities.hideLoadingBar();
                isStillProcessing = false;

                try {
                    //String status =
                    int status = Integer.parseInt(response.getString("statusCode"));
                    if(status == Configs.sucessStatusCode){
                        JSONObject data = response.getJSONObject("data");
                        JSONObject tradeSettings = data.getJSONObject("tradeSettings");

                        prefManager.setUsdConversionRate(tradeSettings.getString("usdConversionAmount"));
                        prefManager.setUsdDepositRate(tradeSettings.getString("depositConversionRate"));
                        prefManager.setUsdWithdrawRate(tradeSettings.getString("withdrawConversionRate"));
                        prefManager.setDepositLimit(tradeSettings.getString("depositLimit"));
                        prefManager.setDepositLowerLimit(tradeSettings.getString("depositLowerLimit"));

                        prefManager.setMSISDN(helperUtilities.formatNumber(iPhoneNum.getText().toString()));
                        prefManager.setUserId(String.valueOf(data.getInt("userId")));
                        prefManager.setFirstTimeLaunch(false);
                        prefManager.setIsResetingPin(false);

                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();

                    }

                    //loadingSpinKit.setVisibility(View.GONE);
                } catch (JSONException e) {
                    e.printStackTrace();

                }
            }

            @Override
            public void onErrorResponse(JSONObject response) {
                isStillProcessing = false;
                helperUtilities.hideLoadingBar();
                //((ListBillPaymentsServices)getActivity()).hideLoadingLayout();

                Log.d("action:", "error response from api..");

                if(response.isNull("statusDescription")) {
                    helperUtilities.showErrorMessage(Login.this, response.toString());
                }else{
                    try {
                        //showErrorMessage(response.getString("message"));

                        Log.d("action:", "error response from api..:"+response.getString("statusDescription"));

                        if(response.getInt("statusCode") == Configs.outdatedVersion){
                            SweetAlertDialog pDialog = new SweetAlertDialog(Login.this, SweetAlertDialog.ERROR_TYPE);
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
                            helperUtilities.showErrorMessage(Login.this, response.getString("statusDescription"));
                        }

                        //helperUtilities.showErrorMessage(Login.this, response.getString("statusDescription"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

        },Configs.apiUrl+"users/login");
    }



}