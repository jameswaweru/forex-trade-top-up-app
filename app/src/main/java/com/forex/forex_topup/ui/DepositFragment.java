package com.forex.forex_topup.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.forex.forex_topup.MainActivity;
import com.forex.forex_topup.R;
import com.forex.forex_topup.adapters.ListUserAccountsAdapter;
import com.forex.forex_topup.models.UserAccount;
import com.forex.forex_topup.onboarding.Login;
import com.forex.forex_topup.onboarding.Register;
import com.forex.forex_topup.utils.Configs;
import com.forex.forex_topup.utils.HelperUtilities;
import com.forex.forex_topup.utils.MyDividerItemDecoration;
import com.forex.forex_topup.utils.PrefManager;
import com.forex.forex_topup.utils.ResponseCallback;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DepositFragment extends Fragment
        implements ListUserAccountsAdapter.UserAccountsAdapterListener {

    AlertDialog alertDialog;
    ListUserAccountsAdapter userAccountsAdapter;
    List<UserAccount> userAccountList;

    RelativeLayout selectAccount;
    TextView displaySelectedAccount, displayAmountToReceive, displaydepositLimit;

    private boolean isStillProcessing = false;
    boolean isAccountSelected = false;
    HelperUtilities helperUtilities;
    PrefManager prefManager;
    RelativeLayout submitButtonLayout;
    EditText eAmount;
    Button btnPay;

    public DepositFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_deposit, container, false);

        displaydepositLimit = view.findViewById(R.id.display_deposit_limit);
        displayAmountToReceive = view.findViewById(R.id.display_totals_to_reflect_label);
        prefManager = new PrefManager(getActivity());
        helperUtilities = new HelperUtilities(getActivity());

        btnPay = view.findViewById(R.id.submitButton);
        eAmount = view.findViewById(R.id.input_amount);
        submitButtonLayout = view.findViewById(R.id.btn_button);
        displaySelectedAccount = view.findViewById(R.id.display_selected_account);
        selectAccount = view.findViewById(R.id.account_selected_layout);
        userAccountList = new ArrayList<>();
        userAccountsAdapter = new ListUserAccountsAdapter(getActivity() , userAccountList , this);

        btnPay.setText("Deposit");

        eAmount.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(s.length() != 0){
                    double avgAmnt = Double.parseDouble(eAmount.getText().toString())/Double.parseDouble(prefManager.getUsdDepositRate());
                    //Toast.makeText(getContext(),"avgAmnt:"+avgAmnt,Toast.LENGTH_LONG).show();
                    if( avgAmnt < Double.parseDouble(String.valueOf(prefManager.getDepositLowerLimit()))){
                        String errorMessage = "Deposit limit per transaction is $ "+prefManager.getDepositLowerLimit();
                        Snackbar.make(view, errorMessage, Snackbar.LENGTH_LONG)
                                .setAction("", null)
                                .setBackgroundTint(getResources().getColor(R.color.red_btn_bg_color))
                                .show();
                        //displaydepositLimit.setText("Deposit limit per transaction is $ "+prefManager.getDepositLowerLimit());
                    }else if(Double.parseDouble(eAmount.getText().toString()) > Double.parseDouble(String.valueOf(prefManager.getDepositLimit()))){
                        String errorMessage = "Deposit limit per transaction is KES "+prefManager.getDepositLimit();
                        Snackbar.make(view, errorMessage, Snackbar.LENGTH_LONG)
                                .setAction("", null)
                                .setBackgroundTint(getResources().getColor(R.color.red_btn_bg_color))
                                .show();
                        //displaydepositLimit.setText("Deposit limit per transaction is KES "+prefManager.getDepositLimit());
                    }
                    displayAmountToReceive.setText("You will receive $ "+helperUtilities.roundTruncate(avgAmnt));
                }

            }
        });

        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isStillProcessing){
                    Toast.makeText(getActivity() , "Please wait",Toast.LENGTH_LONG).show();
                    return;
                }

                if(!isAccountSelected){
                    Toast.makeText(getActivity() , "Please select Account",Toast.LENGTH_LONG).show();
                    return;
                }

                if(TextUtils.isEmpty(eAmount.getText().toString())){
                    Toast.makeText(getActivity() , "Enter amount",Toast.LENGTH_LONG).show();
                    return;
                }

                if(!helperUtilities.isNumeric(eAmount.getText().toString())){
                    Toast.makeText(getActivity() , "Amount must be numeric",Toast.LENGTH_LONG).show();
                    return;
                }

                if(Double.parseDouble(eAmount.getText().toString())>Double.parseDouble(prefManager.getDepositLimit())){
                    Toast.makeText(getActivity() , "Deposit limit is KES "+prefManager.getDepositLimit(),Toast.LENGTH_LONG).show();
                    return;
                }

                double avgAmnt = Double.parseDouble(eAmount.getText().toString())/Double.parseDouble(prefManager.getUsdDepositRate());

                if(avgAmnt < Double.parseDouble(String.valueOf(prefManager.getDepositLowerLimit()))){
                    Toast.makeText(getActivity() , "Minimum deposit amount is $ "+prefManager.getDepositLowerLimit(),Toast.LENGTH_LONG).show();
                    return;
                }

                deposit();
            }
        });

        submitButtonLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if(isStillProcessing){
//                    Toast.makeText(getActivity() , "Please wait",Toast.LENGTH_LONG).show();
//                    return;
//                }
//
//                if(!isAccountSelected){
//                    Toast.makeText(getActivity() , "Please select Account",Toast.LENGTH_LONG).show();
//                    return;
//                }
//
//                if(TextUtils.isEmpty(eAmount.getText().toString())){
//                    Toast.makeText(getActivity() , "Enter amount",Toast.LENGTH_LONG).show();
//                    return;
//                }
//
//                if(!helperUtilities.isNumeric(eAmount.getText().toString())){
//                    Toast.makeText(getActivity() , "Amount must be numeric",Toast.LENGTH_LONG).show();
//                    return;
//                }
//
//                deposit();

            }
        });

        selectAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayUserAccounts(view);
            }
        });

       fetchUserAccounts();

        return view;
    }



    public void deposit(){
        isStillProcessing = true;
        helperUtilities.showLoadingBar(getActivity() , "Processing.");

        final Map<String, Object> postParam = new HashMap<String, Object>();
        postParam.put("msisdn" , prefManager.getMSISDN());
        postParam.put("checkoutMsisdn" , prefManager.getMSISDN());
        postParam.put("accountNumber" , displaySelectedAccount.getText().toString());
        postParam.put("amount", eAmount.getText().toString());
        postParam.put("paymentCode","4074723");
        postParam.put("userId", prefManager.getUserId());

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
                        Toast.makeText(getContext(),"Enter mpesa pin to top up", Toast.LENGTH_LONG).show();
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
                    helperUtilities.showErrorMessage(getContext(), response.toString());
                }else{
                    try {
                        //showErrorMessage(response.getString("message"));

                        Log.d("action:", "error response from api..:"+response.getString("statusDescription"));

                        helperUtilities.showErrorMessage(getContext(), response.getString("statusDescription"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

        },Configs.apiUrl+"payments/pay");
    }

    public void fetchUserAccounts(){
        isStillProcessing = true;
        helperUtilities.showLoadingBar(getActivity() , "Fetching accounts.");

        final Map<String, Object> postParam = new HashMap<String, Object>();


        helperUtilities.volleyHttpGetRequestV2(postParam, new ResponseCallback() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onResponse(JSONObject response) {

                helperUtilities.hideLoadingBar();
                isStillProcessing = false;

                try {
                    //String status =
                    int status = Integer.parseInt(response.getString("statusCode"));
                    if(status == Configs.sucessStatusCode){
                        JSONArray data = response.getJSONArray("data");
                        loadUserAccountsIntoAdapter(data);
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
                    helperUtilities.showErrorMessage(getContext(), response.toString());
                }else{
                    try {
                        //showErrorMessage(response.getString("message"));

                        Log.d("action:", "error response from api..:"+response.getString("statusDescription"));

                        helperUtilities.showErrorMessage(getContext(), response.getString("statusDescription"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

        },Configs.apiUrl+"users/getUserTradingAccounts/"+prefManager.getUserId());
    }

    private void loadUserAccountsIntoAdapter(JSONArray data){

        if(data.length() > 0){

            for (int ij = 0; ij < data.length(); ij++) {
                JSONObject details = null;
                try {
                    details = (JSONObject) data
                            .get(ij);


                    UserAccount userAccount = new UserAccount(details.getInt("userAccountId"), details.getString("accountNumber"));
                    userAccountList.add(userAccount);



                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }


        userAccountsAdapter.notifyDataSetChanged();
    }


    private void displayUserAccounts(View v) {

        //loadUserAccountsIntoAdapter();

        RecyclerView listOptionsRecyclerview;
        final AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext(), R.style.CustomAlertDialog);
        ViewGroup viewGroup = v.findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(v.getContext()).inflate(R.layout.list_options_list_services_dropdown, viewGroup, false);
        //Button buttonOk=dialogView.findViewById(R.id.buttonOk);

        final TextView selectServiceProvidersTextview = dialogView.findViewById(R.id.select_service_title);
        listOptionsRecyclerview = dialogView.findViewById(R.id.services_list_RecyclerView);

        selectServiceProvidersTextview.setText("Select Account");

        RecyclerView.LayoutManager mLayoutManager2 = new LinearLayoutManager(getActivity());
        listOptionsRecyclerview.setLayoutManager(mLayoutManager2);
        listOptionsRecyclerview.setItemAnimator(new DefaultItemAnimator());
        listOptionsRecyclerview.addItemDecoration(new MyDividerItemDecoration(v.getContext(), DividerItemDecoration.VERTICAL, 6));
        listOptionsRecyclerview.setAdapter(userAccountsAdapter);


        builder.setView(dialogView);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });
        alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onUserAccountSelected(UserAccount option) {
        isAccountSelected = true;
        displaySelectedAccount.setText(option.getAccountNumber());
        alertDialog.cancel();
    }
}