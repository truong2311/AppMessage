package com.example.mobilemessagingapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mobilemessagingapp.R;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {

    private Button btnCancelFP, btnConfirmFP;
    private EditText edtEmailFP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        mapping();
        OnClick();
    }

    private void mapping() {
        btnCancelFP = findViewById(R.id.btnCancelFP);
        btnConfirmFP = findViewById(R.id.btnConfirmFP);
        edtEmailFP = findViewById(R.id.edtEmailFP);
    }

    private void OnClick() {
        btnCancelFP.setOnClickListener(view -> {
            Intent intent = new Intent(ForgotPassword.this, SignIn.class);
            startActivity(intent);
            finish();
        });

        btnConfirmFP.setOnClickListener(view -> {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            String emailAddress = edtEmailFP.getText().toString().trim();

            auth.sendPasswordResetEmail(emailAddress)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(ForgotPassword.this, SignIn.class);
                            startActivity(intent);
                            Toast.makeText(ForgotPassword.this, R.string.check_your_email, Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }
}