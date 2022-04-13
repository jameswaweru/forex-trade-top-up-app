package com.forex.forex_topup.ui;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.forex.forex_topup.R;
import com.forex.forex_topup.adapters.ListTransactionsAdapter;
import com.forex.forex_topup.models.Transactions;
import com.forex.forex_topup.models.UserAccount;
import com.forex.forex_topup.utils.Configs;
import com.forex.forex_topup.utils.HelperUtilities;
import com.forex.forex_topup.utils.PrefManager;
import com.forex.forex_topup.utils.ResponseCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransactionsFragment extends Fragment
        implements ListTransactionsAdapter.ListTransactionsAdapterListener {

    private ListTransactionsAdapter listTransactionsAdapter;
    private List<Transactions> transactionsList;
    private RecyclerView transactionSummaryRecyclerview;

    private boolean isStillProcessing = false;
    HelperUtilities helperUtilities;
    PrefManager prefManager;

    public TransactionsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_transactions, container, false);

        transactionsList = new ArrayList<>();
        listTransactionsAdapter = new ListTransactionsAdapter(getActivity() , transactionsList , this);
        transactionSummaryRecyclerview = view.findViewById(R.id.transaction_receipts_recyclerview);
        prefManager = new PrefManager(getActivity());
        helperUtilities = new HelperUtilities(getActivity());

        RecyclerView.LayoutManager mLayoutManager2 = new LinearLayoutManager(getActivity());
        transactionSummaryRecyclerview.setLayoutManager(mLayoutManager2);
        transactionSummaryRecyclerview.setItemAnimator(new DefaultItemAnimator());
        //transactionSummaryRecyclerview.addItemDecoration(new MyDividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL, 6));
        transactionSummaryRecyclerview.setAdapter(listTransactionsAdapter);

        fetchUserPayments();


        return view;
    }


    public void fetchUserPayments(){
        isStillProcessing = true;
        helperUtilities.showLoadingBar(getActivity() , "Fetching transactions.");

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
                        loadTransactions(data);
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

        },Configs.apiUrl+"payments/getUserPayments/"+prefManager.getUserId()); //+prefManager.getUserId()
    }

    private void loadTransactions(JSONArray data){

        if(data.length() > 0){
            for (int ij = 0; ij < data.length(); ij++) {
                JSONObject details = null;
                try {
                    details = (JSONObject) data
                            .get(ij);

                    JSONObject requestLog = details.getJSONObject("requestLog");


                    Transactions transactionTwo = new Transactions();
                    transactionTwo.setTransactionID(details.getString("paymentId"));
                    transactionTwo.setTransactionAmount("KES "+details.getString("amount"));
                    transactionTwo.setTransactionDate(details.getString("dateCreated"));
                    transactionTwo.setTransactionIcon("");
                    transactionTwo.setTransactionMsisdn(details.getString("checkoutMsisdn"));
                    transactionTwo.setTransactionText(details.getString("transactionType"));
                    transactionTwo.setTransactionTime(details.getString("dateCreated"));
                    transactionTwo.setTransactionStatusID(details.getString("paymentStatus"));
                    transactionTwo.setTransactionAccountNumber("Ac: "+details.getString("accountNumber"));
                    transactionTwo.setTransactionServiceName(details.getString("transactionType"));
                    transactionTwo.setTransactionServiceCategoryID("0");
                    transactionTwo.setTransactionServiceCategoryName(details.getString("transactionType"));

                    transactionsList.add(transactionTwo);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

//        Transactions transactionOne = new Transactions();
//        transactionOne.setTransactionID("1");
//        transactionOne.setTransactionAmount("KES 12,000");
//        transactionOne.setTransactionDate("Thur 01, 2014");
//        transactionOne.setTransactionIcon("");
//        transactionOne.setTransactionMsisdn("254726765977");
//        transactionOne.setTransactionText("Withdrawal-");
//        transactionOne.setTransactionTime("Time");
//        transactionOne.setTransactionStatusID("1");
//        transactionOne.setTransactionAccountNumber("Ac:123212121");
//        transactionOne.setTransactionServiceName("Deposit");
//        transactionOne.setTransactionServiceCategoryID("0");
//        transactionOne.setTransactionServiceCategoryName("Deposit");
//
//
//        Transactions transactionTwo = new Transactions();
//        transactionTwo.setTransactionID("1");
//        transactionTwo.setTransactionAmount("KES 12,000");
//        transactionTwo.setTransactionDate("Thur 01, 2014");
//        transactionTwo.setTransactionIcon("");
//        transactionTwo.setTransactionMsisdn("254726765977");
//        transactionTwo.setTransactionText("Deposit-");
//        transactionTwo.setTransactionTime("Time");
//        transactionTwo.setTransactionStatusID("1");
//        transactionTwo.setTransactionAccountNumber("Ac:123212121");
//        transactionTwo.setTransactionServiceName("Deposit");
//        transactionTwo.setTransactionServiceCategoryID("0");
//        transactionTwo.setTransactionServiceCategoryName("Deposit");
//
//        transactionsList.add(transactionTwo);
//        transactionsList.add(transactionOne);

        listTransactionsAdapter.notifyDataSetChanged();


    }

    @Override
    public void onTransactionSelected(Transactions option) {

    }
}