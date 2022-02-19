package com.forex.forex_topup.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.forex.forex_topup.R;
import com.forex.forex_topup.adapters.ListTransactionsAdapter;
import com.forex.forex_topup.models.Transactions;

import java.util.ArrayList;
import java.util.List;

public class TransactionsFragment extends Fragment
        implements ListTransactionsAdapter.ListTransactionsAdapterListener {

    private ListTransactionsAdapter listTransactionsAdapter;
    private List<Transactions> transactionsList;
    private RecyclerView transactionSummaryRecyclerview;

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

        RecyclerView.LayoutManager mLayoutManager2 = new LinearLayoutManager(getActivity());
        transactionSummaryRecyclerview.setLayoutManager(mLayoutManager2);
        transactionSummaryRecyclerview.setItemAnimator(new DefaultItemAnimator());
        //transactionSummaryRecyclerview.addItemDecoration(new MyDividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL, 6));
        transactionSummaryRecyclerview.setAdapter(listTransactionsAdapter);

        loadTransactions();


        return view;
    }

    private void loadTransactions(){

        Transactions transactionOne = new Transactions();
        transactionOne.setTransactionID("1");
        transactionOne.setTransactionAmount("KES 12,000");
        transactionOne.setTransactionDate("Thur 01, 2014");
        transactionOne.setTransactionIcon("");
        transactionOne.setTransactionMsisdn("254726765977");
        transactionOne.setTransactionText("Withdrawal-");
        transactionOne.setTransactionTime("Time");
        transactionOne.setTransactionStatusID("1");
        transactionOne.setTransactionAccountNumber("Ac:123212121");
        transactionOne.setTransactionServiceName("Deposit");
        transactionOne.setTransactionServiceCategoryID("0");
        transactionOne.setTransactionServiceCategoryName("Deposit");


        Transactions transactionTwo = new Transactions();
        transactionTwo.setTransactionID("1");
        transactionTwo.setTransactionAmount("KES 12,000");
        transactionTwo.setTransactionDate("Thur 01, 2014");
        transactionTwo.setTransactionIcon("");
        transactionTwo.setTransactionMsisdn("254726765977");
        transactionTwo.setTransactionText("Deposit-");
        transactionTwo.setTransactionTime("Time");
        transactionTwo.setTransactionStatusID("1");
        transactionTwo.setTransactionAccountNumber("Ac:123212121");
        transactionTwo.setTransactionServiceName("Deposit");
        transactionTwo.setTransactionServiceCategoryID("0");
        transactionTwo.setTransactionServiceCategoryName("Deposit");

        transactionsList.add(transactionTwo);
        transactionsList.add(transactionOne);

        listTransactionsAdapter.notifyDataSetChanged();


    }

    @Override
    public void onTransactionSelected(Transactions option) {

    }
}