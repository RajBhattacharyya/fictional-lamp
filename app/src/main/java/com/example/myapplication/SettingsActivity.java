package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SettingsActivity extends AppCompatActivity {
    TextView mail;
    Button nextb;
    Button Image;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        mail = findViewById(R.id.mail);
        nextb = findViewById(R.id.nextbtn);
        Image = findViewById(R.id.button5);
        getUserProfile();
        nextb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSettingsActivity();
            }
        });
        Image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startImageView();
            }
        });
    }

    public void getUserProfile() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String emailp = user.getEmail();
            mail.setText(emailp);
        }
    }
    private void startSettingsActivity() {
        Intent intent = new Intent(SettingsActivity.this, MonitorActivity.class);
        startActivity(intent);
    }

    private void startImageView() {
        Intent intent = new Intent(SettingsActivity.this, ImageCapture.class);
        startActivity(intent);
    }
}
