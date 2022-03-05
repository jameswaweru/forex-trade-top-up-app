package com.forex.forex_topup.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.forex.forex_topup.R;
import com.forex.forex_topup.onboarding.Login;
import com.forex.forex_topup.utils.PrefManager;

public class LogOut extends Fragment {

    Button logOutButton;
    PrefManager prefManager;

    public LogOut() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_log_out, container, false);

        prefManager = new PrefManager(getContext());
        logOutButton = view.findViewById(R.id.submitButton);
        logOutButton.setText("Log Out");
        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prefManager.setMSISDN("");
                prefManager.setUsdConversionRate("0");
                prefManager.setFirstTimeLaunch(true);
                prefManager.setIsResetingPin(false);

                startActivity(new Intent(getActivity() , Login.class));
                getActivity().finish();

            }
        });


        return view;
    }
}