package com.forex.forex_topup.ui;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.forex.forex_topup.R;
import com.forex.forex_topup.adapters.ListUserAccountsAdapter;
import com.forex.forex_topup.models.UserAccount;
import com.forex.forex_topup.utils.MyDividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

public class DepositFragment extends Fragment
        implements ListUserAccountsAdapter.UserAccountsAdapterListener {

    AlertDialog alertDialog;
    ListUserAccountsAdapter userAccountsAdapter;
    List<UserAccount> userAccountList;

    RelativeLayout selectAccount;
    TextView displaySelectedAccount;

    public DepositFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_deposit, container, false);

        displaySelectedAccount = view.findViewById(R.id.display_selected_account);
        selectAccount = view.findViewById(R.id.account_selected_layout);
        userAccountList = new ArrayList<>();
        userAccountsAdapter = new ListUserAccountsAdapter(getActivity() , userAccountList , this);

        selectAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayUserAccounts(view);
            }
        });

       loadUserAccountsIntoAdapter();

        return view;
    }

    private void loadUserAccountsIntoAdapter(){
        userAccountList.add(new UserAccount(1,"CR-123456"));
        userAccountList.add(new UserAccount(2,"CR-321456"));
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
        alertDialog.cancel();
    }
}