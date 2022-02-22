package com.forex.forex_topup.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.forex.forex_topup.R;
import com.forex.forex_topup.adapters.ListUserAccountsAdapter;
import com.forex.forex_topup.models.UserAccount;
import com.forex.forex_topup.utils.MyDividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

public class UserAccountsFragment extends Fragment implements ListUserAccountsAdapter.UserAccountsAdapterListener {

    RelativeLayout displayAccountsLayout , addAccountLayout , toggleAddAccountLayout;
    RecyclerView listAccountsRecyclerview;

    ListUserAccountsAdapter userAccountsAdapter;
    List<UserAccount> userAccountList;

    boolean isAddAccountShowing = false;

    public UserAccountsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_accounts, container, false);

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
        });

        loadUserAccountsIntoAdapter();

        return view;
    }

    private void loadUserAccountsIntoAdapter(){
        userAccountList.add(new UserAccount(1,"CR-123456"));
        userAccountList.add(new UserAccount(2,"CR-321456"));
        userAccountsAdapter.notifyDataSetChanged();
    }


    @Override
    public void onUserAccountSelected(UserAccount option) {

    }
}