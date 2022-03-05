package com.forex.forex_topup.onboarding;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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

public class GenerateSmsOtp extends AppCompatActivity {

    Button btnGetOtp;
    ImageView backButton;
    TextView toolBarText;
    private boolean isStillProcessing = false;
    HelperUtilities helperUtilities;
    PrefManager prefManager;
    EditText iPhoneNum, iPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_sms_otp);

        iPhoneNum = findViewById(R.id.input_msisdn);
        helperUtilities = new HelperUtilities(getApplicationContext());
        prefManager = new PrefManager(getApplicationContext());
        btnGetOtp = findViewById(R.id.submitButton);
        toolBarText = findViewById(R.id.toolbar_text);
        backButton = findViewById(R.id.back_btn);
        toolBarText.setText("Generate Otp");

        iPhoneNum.setText(prefManager.getMSISDN());

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnGetOtp.setOnClickListener(new View.OnClickListener() {
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

                generateSmsOtp();

            }
        });

    }


    public void generateSmsOtp(){
        isStillProcessing = true;
        helperUtilities.showLoadingBar(GenerateSmsOtp.this , "Generating otp...");

        final Map<String, Object> postParam = new HashMap<String, Object>();
        postParam.put("msisdn" , iPhoneNum.getText().toString());


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

                        prefManager.setIsResetingPin(true);
                        prefManager.setMSISDN(helperUtilities.formatNumber(iPhoneNum.getText().toString()));
                        startActivity(new Intent(getApplicationContext(), ResetPassword.class));
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
                    helperUtilities.showErrorMessage(GenerateSmsOtp.this, response.toString());
                }else{
                    try {
                        //showErrorMessage(response.getString("message"));

                        Log.d("action:", "error response from api..:"+response.getString("statusDescription"));

                        helperUtilities.showErrorMessage(GenerateSmsOtp.this, response.getString("statusDescription"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

        },Configs.apiUrl+"otp/generateSmsOtp");
    }


}