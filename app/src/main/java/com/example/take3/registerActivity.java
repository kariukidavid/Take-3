package com.example.take3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class registerActivity extends AppCompatActivity {
    private EditText editTextRegisterEmail, editTextRegisterPassword, editTextRegisterConfirmPassword, editTextRegisterName;
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Register");
        }

        editTextRegisterConfirmPassword = findViewById(R.id.editText_register_confirm_password);
        editTextRegisterEmail = findViewById(R.id.editText_register_email);
        editTextRegisterPassword = findViewById(R.id.editText_register_password);
        editTextRegisterName = findViewById(R.id.editText_register_full_name);
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        Button buttonRegister = findViewById(R.id.button);
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editTextRegisterEmail.getText().toString();
                String password = editTextRegisterPassword.getText().toString();
                String confirmPassword = editTextRegisterConfirmPassword.getText().toString();
                String name = editTextRegisterName.getText().toString();

                if (validateInput(name, email, password, confirmPassword)) {
                    registerUser(name, email, password);
                }
            }
        });
    }

    private boolean validateInput(String name, String email, String password, String confirmPassword) {
        if (TextUtils.isEmpty(name)) {
            editTextRegisterName.setError("Name is required");
            editTextRegisterName.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(email)) {
            editTextRegisterEmail.setError("Email is required");
            editTextRegisterEmail.requestFocus();
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextRegisterEmail.setError("Invalid email format");
            editTextRegisterEmail.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            editTextRegisterPassword.setError("Password is required");
            editTextRegisterPassword.requestFocus();
            return false;
        }
        if (password.length() < 6) {
            editTextRegisterPassword.setError("Password is too short");
            editTextRegisterPassword.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(confirmPassword)) {
            editTextRegisterConfirmPassword.setError("Confirm Password is required");
            editTextRegisterConfirmPassword.requestFocus();
            return false;
        }
        if (!password.equals(confirmPassword)) {
            editTextRegisterConfirmPassword.setError("Passwords do not match");
            editTextRegisterConfirmPassword.requestFocus();
            return false;
        }
        return true;
    }

    private void registerUser(final String name, final String email, String password) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser firebaseUser = auth.getCurrentUser();
                    if (firebaseUser != null) {
                        firebaseUser.sendEmailVerification();
                        saveUser(name, email);
                    }
                } else {
                    Toast.makeText(registerActivity.this, "Registration failed. Please try again.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void saveUser(String name, String email) {
        Map<String, Object> user = new HashMap<>();
        user.put("name", name);
        user.put("email", email);

        db.collection("users").document(email).set(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(registerActivity.this, "User Registered. Please check your email for verification.", Toast.LENGTH_SHORT).show();
                            // Redirect to login activity
                            Intent intent = new Intent(registerActivity.this, Login.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(registerActivity.this, "Failed to save user data in Firestore.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
