package com.forex.forex_topup.ui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.forex.forex_topup.MainActivity;
import com.forex.forex_topup.R;
import com.forex.forex_topup.adapters.ListUserAccountsAdapter;
import com.forex.forex_topup.models.UserAccount;
import com.forex.forex_topup.onboarding.Login;
import com.forex.forex_topup.utils.Configs;
import com.forex.forex_topup.utils.HelperUtilities;
import com.forex.forex_topup.utils.MyDividerItemDecoration;
import com.forex.forex_topup.utils.PrefManager;
import com.forex.forex_topup.utils.ResponseCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserAccountsFragment extends Fragment implements ListUserAccountsAdapter.UserAccountsAdapterListener {

    RelativeLayout displayAccountsLayout , addAccountLayout , toggleAddAccountLayout;
    RecyclerView listAccountsRecyclerview;

    ListUserAccountsAdapter userAccountsAdapter;
    List<UserAccount> userAccountList;

    boolean isAddAccountShowing = false;
    private boolean isStillProcessing = false;
    boolean isAccountSelected = false;
    HelperUtilities helperUtilities;
    PrefManager prefManager;
    EditText iAccountNumber;
    Button btnAddAccount;

    public UserAccountsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_accounts, container, false);

        btnAddAccount = view.findViewById(R.id.submitButton);
        iAccountNumber = view.findViewById(R.id.input_account_number);
        prefManager = new PrefManager(getActivity());
        helperUtilities = new HelperUtilities(getActivity());
        userAccountList = new ArrayList<>();
        userAccountsAdapter = new ListUserAccountsAdapter(getActivity() , userAccountList , this);

        listAccountsRecyclerview = view.findViewById(R.id.user_accounts_recyclerview);
        toggleAddAccountLayout = view.findViewById(R.id.add_new_account_button);
        addAccountLayout = view.findViewById(R.id.add_account_layout);

        RecyclerView.LayoutManager mLayoutManager2 = new LinearLayoutManager(getActivity());
        listAccountsRecyclerview.setLayoutManager(mLayoutManager2);
        listAccountsRecyclerview.setItemAnimator(new DefaultItemAnimator());
        listAccountsRecyclerview.addItemDecoration(new MyDividerItemDecoration(view.getContext(), DividerItemDecoration.VERTICAL, 6));
        listAccountsRecyclerview.setAdapter(userAccountsAdapter);

        toggleAddAccountLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleScenes();
            }
        });

        //loadUserAccountsIntoAdapter();

        btnAddAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addTradingAccount();
            }
        });

        fetchUserAccounts();

        return view;
    }


    private void toggleScenes(){
        if(isAddAccountShowing){
            listAccountsRecyclerview.setVisibility(View.VISIBLE);
            addAccountLayout.setVisibility(View.GONE);
            isAddAccountShowing = false;
        }else{
            listAccountsRecyclerview.setVisibility(View.GONE);
            addAccountLayout.setVisibility(View.VISIBLE);
            isAddAccountShowing = true;
        }
    }


    public void addTradingAccount(){
        isStillProcessing = true;
        helperUtilities.showLoadingBar(getContext() , "Adding account..");

        final Map<String, Object> postParam = new HashMap<String, Object>();
        postParam.put("accountNumber" , iAccountNumber.getText().toString());
        postParam.put("userId" , prefManager.getUserId());

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
                        toggleScenes();
                        fetchUserAccounts();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();

                }
            }

            @Override
            public void onErrorResponse(JSONObject response) {
                isStillProcessing = false;
                helperUtilities.hideLoadingBar();

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

        },Configs.apiUrl+"users/addUserTradingAccount");
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


    @Override
    public void onUserAccountSelected(UserAccount option) {

    }
}