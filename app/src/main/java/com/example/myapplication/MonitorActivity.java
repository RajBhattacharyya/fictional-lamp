package com.example.myapplication;

import java.lang.Math;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MonitorActivity extends AppCompatActivity {

    private TextView statusp;
    private TextView latitudeTextView;
    private TextView longitudeTextView;
    private Switch statusd;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference mRootReference;
    private DatabaseReference mChildReferenceStatusp;
    private DatabaseReference mChildReferenceStatusd;
    private FusedLocationProviderClient fusedLocationClient;

    private TextView statusTextView;
    private Button showImageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.monitor);

        statusp = findViewById(R.id.status);
        statusd = findViewById(R.id.door);
        latitudeTextView = findViewById(R.id.latitude);
        longitudeTextView = findViewById(R.id.longitude);
        statusTextView= findViewById(R.id.status1);
        showImageButton = findViewById(R.id.showImageButton);

        firebaseDatabase = FirebaseDatabase.getInstance();
        mRootReference = firebaseDatabase.getReference();
        mChildReferenceStatusp = mRootReference.child("statusp");
        mChildReferenceStatusd = mRootReference.child("statusd");
        statusp.setText("");

        // Initialize FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getLocation();

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

        showImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start IntruderImageActivity to display the intruder image
                Intent intent = new Intent(MonitorActivity.this, IntruderImageActivity.class);
                startActivity(intent);
            }
        });

        checkForIntruders();
    }

    private void getLocation() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000); // 10 seconds

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null) {
                    Location location = locationResult.getLastLocation();
                    if (location != null) {
                        updateLocationUI(location);
                    }
                }
            }
        }, null);
    }

    private void checkForIntruders() {
        DatabaseReference intruderRef = FirebaseDatabase.getInstance().getReference().child("Intruder");
        intruderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String intruderStatus = dataSnapshot.getValue(String.class);
                if (intruderStatus != null && intruderStatus.equals("ok")) {
                    statusTextView.setText("Status: No intruders detected");
                    statusTextView.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                    showImageButton.setVisibility(View.GONE);
                } else if (intruderStatus != null && intruderStatus.equals("intruders detected")) {
                    statusTextView.setText("Status: Intruders detected");
                    statusTextView.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                    showImageButton.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
                Toast.makeText(MonitorActivity.this, "Failed to check for intruders", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateLocationUI(Location location) {
        if (location != null) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            latitudeTextView.setText("Latitude: " + latitude);
            longitudeTextView.setText("Longitude: " + longitude);
            latitude = Math.toRadians(latitude);
            longitude = Math.toRadians(longitude);
            double lat2 = Math.toRadians(22.4828636);
            double lon2 = Math.toRadians(88.3172615);

            // Haversine formula
            double dlat = lat2 - latitude;
            double dlon = lon2 - longitude;
            double a = Math.pow(Math.sin(dlat / 2), 2) + Math.cos(latitude) * Math.cos(lat2) * Math.pow(Math.sin(dlon / 2), 2);
            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
            double distance = 6371 * c;


            mRootReference.child("lat").setValue(latitude);


            mRootReference.child("long").setValue(longitude);

            if(distance>1){
                mRootReference.child("statusd").setValue(1);
            }
            else{
                mRootReference.child("statusd").setValue(0);
            }
        }
    }

}
