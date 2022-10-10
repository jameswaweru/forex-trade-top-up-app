package com.forex.forex_topup.onboarding;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.forex.forex_topup.R;
import com.forex.forex_topup.utils.Configs;
import com.forex.forex_topup.utils.HelperUtilities;
import com.forex.forex_topup.utils.PrefManager;
import com.forex.forex_topup.utils.ResponseCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ResetPassword extends AppCompatActivity {

    Button btnReset;
    ImageView backButton;
    TextView toolBarText, displayResetPinCodeHeaderText;
    private boolean isStillProcessing = false;
    HelperUtilities helperUtilities;
    PrefManager prefManager;
    EditText  iPhoneNum, iFirstPassword, iLastPassword, iOtp, iEmail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);


        iEmail = findViewById(R.id.input_email);
        displayResetPinCodeHeaderText = findViewById(R.id._reset_otp_header);
        iOtp = findViewById(R.id.input_otp);
        iLastPassword = findViewById(R.id.input_second_password);
        iFirstPassword = findViewById(R.id.input_password);
        iPhoneNum = findViewById(R.id.input_msisdn);
        helperUtilities = new HelperUtilities(getApplicationContext());
        prefManager = new PrefManager(getApplicationContext());

        btnReset = findViewById(R.id.submitButton);
        toolBarText = findViewById(R.id.toolbar_text);
        backButton = findViewById(R.id.back_btn);
        toolBarText.setText("Set New Pin");
        displayResetPinCodeHeaderText.setText("Use the otp we sent to "+prefManager.getEmail());
        iPhoneNum.setText(prefManager.getMSISDN());
        iEmail.setText(prefManager.getEmail());
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prefManager.setIsResetingPin(false);
                prefManager.setFirstTimeLaunch(true);
                startActivity(new Intent(getApplicationContext() , Login.class));
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(isStillProcessing){
                    Toast.makeText(getApplicationContext() , "Please wait",Toast.LENGTH_LONG).show();
                    return;
                }

                if(TextUtils.isEmpty(iEmail.getText().toString())){
                    Toast.makeText(getApplicationContext() , "Enter email that received otp",Toast.LENGTH_LONG).show();
                    return;
                }

                if(TextUtils.isEmpty(iOtp.getText().toString())){
                    Toast.makeText(getApplicationContext() , "Enter otp",Toast.LENGTH_LONG).show();
                    return;
                }

                if(TextUtils.isEmpty(iPhoneNum.getText().toString())){
                    Toast.makeText(getApplicationContext() , "Enter phone number",Toast.LENGTH_LONG).show();
                    return;
                }


                if(TextUtils.isEmpty(iFirstPassword.getText().toString())){
                    Toast.makeText(getApplicationContext() , "Enter password",Toast.LENGTH_LONG).show();
                    return;
                }

                if(TextUtils.isEmpty(iLastPassword.getText().toString())){
                    Toast.makeText(getApplicationContext() , "Re enter password to compare",Toast.LENGTH_LONG).show();
                    return;
                }

                if(!iFirstPassword.getText().toString().equals(iLastPassword.getText().toString())){
                    Toast.makeText(getApplicationContext() , "Passwords doesnt match",Toast.LENGTH_LONG).show();
                    return;
                }




                resetPin();
            }
        });

    }


    public void resetPin(){
        isStillProcessing = true;
        helperUtilities.showLoadingBar(ResetPassword.this , "Processing..");

        final Map<String, Object> postParam = new HashMap<String, Object>();
        postParam.put("msisdn" , iPhoneNum.getText().toString());
        postParam.put("password",iFirstPassword.getText().toString());
        postParam.put("otp",iOtp.getText().toString());
        postParam.put("email", iEmail.getText().toString());


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

                        prefManager.setMSISDN(helperUtilities.formatNumber(iPhoneNum.getText().toString()));

                        startActivity(new Intent(getApplicationContext(), Login.class));
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
                    helperUtilities.showErrorMessage(ResetPassword.this, response.toString());
                }else{
                    try {
                        //showErrorMessage(response.getString("message"));

                        Log.d("action:", "error response from api..:"+response.getString("statusDescription"));

                        //helperUtilities.showErrorMessage(ResetPassword.this, response.getString("statusDescription"));

                        if(response.getInt("statusCode") == Configs.outdatedVersion){
                            SweetAlertDialog pDialog = new SweetAlertDialog(ResetPassword.this, SweetAlertDialog.ERROR_TYPE);
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
                            helperUtilities.showErrorMessage(ResetPassword.this, response.getString("statusDescription"));
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

        },Configs.apiUrl+"users/resetPin");
    }


}