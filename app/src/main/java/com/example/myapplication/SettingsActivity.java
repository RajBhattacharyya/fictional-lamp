package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SettingsActivity extends AppCompatActivity {
    TextView name, mail;
    Button signOutBtn;
    Button nextb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings); // assuming your layout file is named activity_settings.xml

        name = findViewById(R.id.name); // assuming you have a TextView with id textView_name in your layout
        mail = findViewById(R.id.mail); // assuming you have a TextView with id textView_email in your layout
        signOutBtn = findViewById(R.id.signout);
        nextb = findViewById(R.id.nextbtn);
        getUserProfile();
        nextb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSettingsActivity();
            }
        });
    }

    public void getUserProfile() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String namep = user.getDisplayName();
            String emailp = user.getEmail();
            name.setText(namep);
            mail.setText(emailp);
        }
    }
    private void startSettingsActivity() {
        Intent intent = new Intent(SettingsActivity.this, MonitorActivity.class);
        startActivity(intent);
    }
}
