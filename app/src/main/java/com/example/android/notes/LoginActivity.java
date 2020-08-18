package com.example.android.notes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private EditText emailEdittext, passwordEdittext;
    private MaterialButton loginButton;
    private TextView forgotPasswordTextview, signUpTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        setIDs();
        onClickListeners();
    }

    private void onClickListeners() {
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });
        signUpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));

            }
        });
    }

    private void login() {

        String email = emailEdittext.getText().toString().trim();
        String password = passwordEdittext.getText().toString().trim();

        if (email.isEmpty()) {
            emailEdittext.setError("Email Required");
            emailEdittext.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEdittext.setError("Enter a valid email address!");
            emailEdittext.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            passwordEdittext.setError("Password Required");
            passwordEdittext.requestFocus();
            return;
        }

        if (password.length() < 6) {
            passwordEdittext.setError("Minimum password length is 6");
            passwordEdittext.requestFocus();
            return;
        }

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        } else {
                            Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void setIDs() {

        auth = FirebaseAuth.getInstance();
        emailEdittext = findViewById(R.id.email_edittext);
        passwordEdittext = findViewById(R.id.password_edittext);
        loginButton = findViewById(R.id.login_button);
        forgotPasswordTextview = findViewById(R.id.forgot_password);
        signUpTextView = findViewById(R.id.sign_up_text);

    }
}
