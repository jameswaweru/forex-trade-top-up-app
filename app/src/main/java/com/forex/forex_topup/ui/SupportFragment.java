package com.forex.forex_topup.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.forex.forex_topup.R;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;


public class SupportFragment extends Fragment {

    private RelativeLayout callCallCenterLayout, defaultToolBar , chatLayout , btnOpenCustomerCareChat,
            btnOpenFbApp, btnOpenEmail;

    public SupportFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_support, container, false);

        btnOpenEmail = view.findViewById(R.id.btn_email_customer_care_layout);
        btnOpenFbApp = view.findViewById(R.id.btn_open_fb_page_layout);
        btnOpenCustomerCareChat = view.findViewById(R.id.open_customer_care_chat);
        chatLayout = view.findViewById(R.id.new_chat_layout);
//        defaultToolBar = view.findViewById(R.id.default_toolbar);
//        prefManager = new PrefManager(getApplicationContext());
        callCallCenterLayout = view.findViewById(R.id.btn_call_customer_care_layout);
//        toolBarText = findViewById(R.id.toolbar_text);
//        toolBarText.setText("Help");
//        backButton = findViewById(R.id.back_btn);

        btnOpenEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(Intent.ACTION_SENDTO);
                    intent.setData(Uri.parse("mailto:")); // only email apps should handle this
                    intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"edwardugi4@gmail.com"});
                    intent.putExtra(Intent.EXTRA_SUBJECT, "App feedback");
                    startActivity(intent);
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(getActivity(), "There are no email client installed on your device.",Toast.LENGTH_LONG).show();
                }
            }
        });




        btnOpenFbApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pageId = "109248157427026";
                String facebookPageUrl = "https://web.facebook.com/Binary-Cash-Mpesa-109248157427026/";

                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/" + pageId));
                    startActivity(intent);
                } catch (Exception e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://web.facebook.com/Binary-Cash-Mpesa-109248157427026/")));
                }
            }
        });

        btnOpenCustomerCareChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Smooch.init(Help.this, APP_TOKEN);
                String url = "https://api.whatsapp.com/send?phone=+254722801514";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
                //startActivity(new Intent(getApplicationContext() , LoadTawkChat.class));
            }
        });

        callCallCenterLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkCallPhonePermission();
            }
        });


        return view;
    }

    private void checkCallPhonePermission() {
        Dexter.withActivity(getActivity())
                .withPermissions(
                        Manifest.permission.CALL_PHONE
                )
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {

                            Intent intent = new Intent(Intent.ACTION_DIAL);
                            intent.setData(Uri.parse("tel:+254722406794"));
                            startActivity(intent);

                            if (ActivityCompat.checkSelfPermission(getActivity(),
                                    Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                return;
                            }

                            startActivity(intent);

                        }
                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // permission is denied permenantly, navigate user to app settings

                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .onSameThread()
                .check();
    }
}