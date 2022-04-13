package com.forex.forex_topup.onboarding;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.forex.forex_topup.MainActivity;
import com.forex.forex_topup.R;
import com.forex.forex_topup.utils.Configs;
import com.forex.forex_topup.utils.HelperUtilities;
import com.forex.forex_topup.utils.PrefManager;
import com.forex.forex_topup.utils.ResponseCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {

    ImageView backButton;
    TextView toolBarText , openLoginPageLink;
    Button btnSubmit;

    private boolean isStillProcessing = false;
    HelperUtilities helperUtilities;
    PrefManager prefManager;
    EditText iFirstName, iLastName,iBinaryAc, iPhoneNum, iFirstPassword, iLastPassword, iIdNumber;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        prefManager = new PrefManager(getApplicationContext());
        iLastPassword = findViewById(R.id.input_second_password);
        iFirstPassword = findViewById(R.id.input_password);
        iPhoneNum = findViewById(R.id.input_msisdn);
        iBinaryAc = findViewById(R.id.input_binary_account);
        iLastName = findViewById(R.id.input_last_name);
        iFirstName = findViewById(R.id.input_first_name);
        iIdNumber = findViewById(R.id.input_id_number);


        helperUtilities = new HelperUtilities(getApplicationContext());


        openLoginPageLink = findViewById(R.id.access_login_screen_link);
        btnSubmit = findViewById(R.id.submitButton);
        toolBarText = findViewById(R.id.toolbar_text);
        backButton = findViewById(R.id.back_btn);
        toolBarText.setText("Register");

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        openLoginPageLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Login.class));
            }
        });

        iBinaryAc.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(s.length() != 0 && s.length()>1){
                    if(!iBinaryAc.getText().toString().startsWith("CR")){
                        Toast.makeText(getApplicationContext(), "Start with CR",Toast.LENGTH_LONG).show();
                    }
                }

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

                if(TextUtils.isEmpty(iBinaryAc.getText().toString())){
                    Toast.makeText(getApplicationContext() , "Enter binary account",Toast.LENGTH_LONG).show();
                    return;
                }

                if(TextUtils.isEmpty(iFirstName.getText().toString())){
                    Toast.makeText(getApplicationContext() , "Enter first name",Toast.LENGTH_LONG).show();
                    return;
                }

                if(TextUtils.isEmpty(iLastName.getText().toString())){
                    Toast.makeText(getApplicationContext() , "Enter last name",Toast.LENGTH_LONG).show();
                    return;
                }

                if(TextUtils.isEmpty(iIdNumber.getText().toString())){
                    Toast.makeText(getApplicationContext() , "Enter id number",Toast.LENGTH_LONG).show();
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

                if(!iBinaryAc.getText().toString().startsWith("CR")){
                    Toast.makeText(getApplicationContext(), "Start with CR",Toast.LENGTH_LONG).show();
                    return;
                }


                registerUser();
            }
        });


    }


    public void registerUser(){
        isStillProcessing = true;
        helperUtilities.showLoadingBar(Register.this , "Registering.");

        final Map<String, Object> postParam = new HashMap<String, Object>();
        postParam.put("firstName" , iFirstName.getText().toString());
        postParam.put("lastName" , iLastName.getText().toString());
        postParam.put("phoneNumber" , iPhoneNum.getText().toString());
        postParam.put("password" , iFirstPassword.getText().toString());
        postParam.put("defaultBinaryAccount" , iBinaryAc.getText().toString());
        postParam.put("idNumber", iIdNumber.getText().toString());


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
                    helperUtilities.showErrorMessage(Register.this, response.toString());
                }else{
                    try {
                        //showErrorMessage(response.getString("message"));

                        Log.d("action:", "error response from api..:"+response.getString("statusDescription"));

                        helperUtilities.showErrorMessage(Register.this, response.getString("statusDescription"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

        },Configs.apiUrl+"users/register");
    }
}