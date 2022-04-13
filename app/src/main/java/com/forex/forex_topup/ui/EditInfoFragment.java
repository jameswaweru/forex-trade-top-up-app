package com.forex.forex_topup.ui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.forex.forex_topup.MainActivity;
import com.forex.forex_topup.R;
import com.forex.forex_topup.onboarding.Login;
import com.forex.forex_topup.utils.Configs;
import com.forex.forex_topup.utils.HelperUtilities;
import com.forex.forex_topup.utils.PrefManager;
import com.forex.forex_topup.utils.ResponseCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EditInfoFragment extends Fragment {

    private boolean isStillProcessing = false;
    HelperUtilities helperUtilities;
    PrefManager prefManager;
    Button btnOkay;
    EditText iPhoneNew, iPhoneNum, iPassword;

    public EditInfoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_info, container, false);

        iPhoneNew = view.findViewById(R.id.input_new_msisdn);
        iPhoneNum = view.findViewById(R.id.input_msisdn);
        iPassword = view.findViewById(R.id.input_password);
        prefManager = new PrefManager(getActivity());
        helperUtilities = new HelperUtilities(getActivity());
        btnOkay = view.findViewById(R.id.submitButton);

        iPhoneNum.setText(prefManager.getMSISDN());
        btnOkay.setText("Change");
        btnOkay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isStillProcessing){
                    Toast.makeText(getActivity() , "Please wait",Toast.LENGTH_LONG).show();
                    return;
                }

                if(TextUtils.isEmpty(iPhoneNum.getText().toString())){
                    Toast.makeText(getActivity() , "Enter phone number",Toast.LENGTH_LONG).show();
                    return;
                }

                if(TextUtils.isEmpty(iPhoneNew.getText().toString())){
                    Toast.makeText(getActivity() , "Enter new phone number",Toast.LENGTH_LONG).show();
                    return;
                }

                if(TextUtils.isEmpty(iPassword.getText().toString())){
                    Toast.makeText(getActivity() , "Enter password",Toast.LENGTH_LONG).show();
                    return;
                }

                changePhoneNumber();
            }
        });

        return view;
    }

    public void changePhoneNumber(){
        isStillProcessing = true;
        helperUtilities.showLoadingBar(getActivity() , "Changing..");

        final Map<String, Object> postParam = new HashMap<String, Object>();
        postParam.put("phoneNumber" , iPhoneNew.getText().toString());
        postParam.put("password" , iPassword.getText().toString());
        postParam.put("userId", prefManager.getUserId()); //prefManager.getUserId()

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
                        prefManager.setMSISDN(helperUtilities.formatNumber(iPhoneNew.getText().toString()));
                        iPhoneNew.setText("");
                        iPhoneNum.setText(prefManager.getMSISDN());
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
                    helperUtilities.showErrorMessage(getActivity(), response.toString());
                }else{
                    try {
                        //showErrorMessage(response.getString("message"));

                        Log.d("action:", "error response from api..:"+response.getString("statusDescription"));

                        helperUtilities.showErrorMessage(getActivity(), response.getString("statusDescription"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

        },Configs.apiUrl+"users/changeMobileNumber");
    }

}