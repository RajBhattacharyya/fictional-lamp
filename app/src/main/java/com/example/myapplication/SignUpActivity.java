package com.example.myapplication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

public class SignUpActivity extends Activity {
    private double latitude,longitude;
    private TextView lats;
    private TextView longs;
    private FusedLocationProviderClient fusedLocationClient;
    private EditText editTextEmail;
   private EditText editTextPassword;
    private EditText editTextName;
    private EditText editConfirmPassword;
    private FirebaseAuth mAuth;
    private DatabaseReference mRootReference;
    private FirebaseDatabase firebaseDatabase;
    private static final String TAG = "EmailPassword";
    @SuppressLint("MissingInflatedId")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.sign_up);

        editTextName = findViewById(R.id.editTextNameSignUp);
        editTextEmail = findViewById(R.id.editTextEmailSignUp);
        editTextPassword = findViewById(R.id.editTextPasswordSignUp);
        editConfirmPassword = findViewById(R.id.editTextConfirmPasswordSignUp);
        Button signUpButton = findViewById(R.id.buttonSignUp);
        Button getloc = findViewById(R.id.getloc);
        lats = findViewById(R.id.Latitudes);
        longs = findViewById(R.id.Longitudes);
        firebaseDatabase = FirebaseDatabase.getInstance();
        mRootReference = firebaseDatabase.getReference();

        getloc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check for location permission
                if (ActivityCompat.checkSelfPermission(SignUpActivity.this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(SignUpActivity.this,
                                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // Request location permission if not granted
                    ActivityCompat.requestPermissions(SignUpActivity.this,
                            new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 100);
                    return;
                }

                // Initialize the FusedLocationProviderClient
                fusedLocationClient = LocationServices.getFusedLocationProviderClient(SignUpActivity.this);

                // Request location updates
                fusedLocationClient.getLastLocation()
                        .addOnSuccessListener(SignUpActivity.this, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                // Got last known location. In some rare situations, this can be null.
                                if (location != null) {
                                    // Extract latitude and longitude
                                    latitude = location.getLatitude();
                                    longitude = location.getLongitude();

                                    // Display latitude and longitude in TextViews
                                    lats.setText("Latitude: " + latitude);
                                    longs.setText("Longitude: " + longitude);
                                } else {
                                    // Handle null location
                                    Toast.makeText(SignUpActivity.this, "Location is null", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the entered email and password
                String email = editTextEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();
                String name = editTextName.getText().toString().trim();
                String conpassword = editConfirmPassword.getText().toString().trim();
                String lats2 = lats.getText().toString();
                String longs2 = longs.getText().toString();

                // Perform basic validation (you should add more thorough validation)
                if (email.isEmpty() || password.isEmpty() || name.isEmpty() || conpassword.isEmpty() || lats2.isEmpty() || longs2.isEmpty() )  {
                    Toast.makeText(SignUpActivity.this, "Please enter all fields", Toast.LENGTH_SHORT).show();
                } else {
                    mRootReference.child("lats").setValue(latitude);
                    mRootReference.child("longs").setValue(longitude);
                    // TODO: Add your authentication logic here
                    // For this example, just display a toast indicating success
                    Toast.makeText(SignUpActivity.this, "Sign up successful!", Toast.LENGTH_SHORT).show();
                    createAccount(email, password);

                }
            }
        });


    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            reload();
        }
    }

    private void createAccount(String email, String password) {
        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                // Call the method to start the Settings activity
                                settingsActivity(email, password);
                            } else {
                                Log.w(TAG, "User is null");
                                Toast.makeText(SignUpActivity.this, "User is null", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void settingsActivity(String email, String password) {
        Intent intent = new Intent(this, SettingsActivity.class);
        intent.putExtra("email", email);
        intent.putExtra("password", password);
        startActivity(intent);
    }

    private void reload() {
    }
}
