package com.forex.forex_topup.onboarding;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.forex.forex_topup.MainActivity;
import com.forex.forex_topup.R;

public class Register extends AppCompatActivity {

    ImageView backButton;
    TextView toolBarText , openLoginPageLink;
    Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        openLoginPageLink = findViewById(R.id.access_login_screen_link);
        btnSubmit = findViewById(R.id.submitButton);
        toolBarText = findViewById(R.id.toolbar_text);
        backButton = findViewById(R.id.back_btn);
        toolBarText.setText("Register");

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        openLoginPageLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Login.class));
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Login.class));
            }
        });




    }
}