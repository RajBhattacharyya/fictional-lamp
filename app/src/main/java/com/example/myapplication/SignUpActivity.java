package com.example.myapplication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SignUpActivity extends Activity {

    private EditText editTextEmail;
    private EditText editTextPassword;
    private  EditText editTextName;
    private EditText editConfirmPassword;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);

        editTextName = findViewById(R.id.editTextNameSignUp);
        editTextEmail = findViewById(R.id.editTextEmailSignUp);
        editTextPassword = findViewById(R.id.editTextPasswordSignUp);
        editConfirmPassword  = findViewById(R.id.editTextConfirmPasswordSignUp);
        Button signUpButton = findViewById(R.id.buttonSignUp);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the entered email and password
                String email = editTextEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();
                String name = editTextName.getText().toString().trim();
                String conpassword = editConfirmPassword.getText().toString().trim();

                // Perform basic validation (you should add more thorough validation)
                if (email.isEmpty() || password.isEmpty() || name.isEmpty() || conpassword.isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "Please enter both email and password", Toast.LENGTH_SHORT).show();
                } else {
                    // TODO: Add your authentication logic here
                    // For this example, just display a toast indicating success
                    Toast.makeText(SignUpActivity.this, "Sign in successful!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
