package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

public class MonitorActivity extends AppCompatActivity {

    private TextView statusp;
    private ImageView img;
    private Switch statusd;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference mRootReference;
    private DatabaseReference mChildReferenceStatusp;
    private DatabaseReference mChildReferenceStatusd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.monitor);
        statusp = findViewById(R.id.status);
        statusd = findViewById(R.id.door);
        firebaseDatabase = FirebaseDatabase.getInstance();
        mRootReference = firebaseDatabase.getReference();
        mChildReferenceStatusp = mRootReference.child("statusp");
        mChildReferenceStatusd = mRootReference.child("statusd");
        statusp.setText("");

        // Listen for changes in statusd
        mChildReferenceStatusd.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Integer status = dataSnapshot.getValue(Integer.class);
                if (status != null) {
                    statusd.setChecked(status == 1);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
            }
        });

        // Listen for changes in statusp
        mChildReferenceStatusp.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Integer message = dataSnapshot.getValue(Integer.class);
                if (message != null) {
                    statusp.setText(String.valueOf(message));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
            }
        });

        // Set up Switch listener to update statusd in the database
        statusd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int status = isChecked ? 1 : 0;
                mChildReferenceStatusd.setValue(status);
            }
        });

    }

}