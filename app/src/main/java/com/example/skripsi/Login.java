package com.example.skripsi;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.skripsi.Admin.Admin;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText inputEmail, inputPassword;
    private Button loginButton;
    private TextView textView;
    private TextView emailError;
    private TextView passwordError;

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            if (currentUser.getEmail().equals("admin@gmail.com")) {
                startActivity(new Intent(Login.this, Admin.class));
            } else {
                startActivity(new Intent(Login.this, MainActivity.class));
            }
            finish();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        inputEmail = findViewById(R.id.email);
        inputPassword = findViewById(R.id.password);
        textView = findViewById(R.id.text1);
        passwordError = findViewById(R.id.passwordError);
        emailError = findViewById(R.id.emailError);
        loginButton = findViewById(R.id.btn1);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to the RegisterActivity (create it if necessary)
                Intent intent = new Intent(Login.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
        loginButton.setOnClickListener(v -> {
            String email = inputEmail.getText().toString();
            String password = inputPassword.getText().toString();

            boolean valid = true;

            // Clear previous errors
            emailError.setVisibility(View.GONE);
            passwordError.setVisibility(View.GONE);

            // Validate email
            if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailError.setText("Please enter a valid email.");
                emailError.setVisibility(View.VISIBLE);
                valid = false;
            }

            // Validate password
            if (password.isEmpty() || password.length() < 6) {
                passwordError.setText("Password must be at least 6 characters.");
                passwordError.setVisibility(View.VISIBLE);
                valid = false;
            }

            if (valid) {
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, task -> {
                            if (task.isSuccessful()) {
                                String userId = mAuth.getCurrentUser().getUid();
                                Log.d("Main Activity", "User ID: " + userId);

                                if (email.equals("admin@gmail.com")) {
                                    startActivity(new Intent(Login.this, Admin.class));
                                } else {
                                    startActivity(new Intent(Login.this, MainActivity.class));
                                }
                                finish();
                            } else {
                                String errorMessage = task.getException() != null ? task.getException().getMessage() : "Unknown error";
                                Log.e("Main Activity", "Authentication failed: " + errorMessage);
                                Toast.makeText(Login.this, "Authentication failed: " + errorMessage, Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });
    }
}