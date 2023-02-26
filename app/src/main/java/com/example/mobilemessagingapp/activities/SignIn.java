package com.example.mobilemessagingapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobilemessagingapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignIn extends AppCompatActivity {

    private Button btnSignIn, btnSignUp;
    private TextInputEditText edtEmailSI, edtPasswordSI;
    private TextInputLayout edtEmailSIWrapper, edtPasswordSIWrapper;
    private TextView tvForgotPass;
    private LinearLayout linearlayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        mapping();
        OnCLick();
    }

    private void mapping() {
        btnSignIn = findViewById(R.id.btnSignIn);
        btnSignUp = findViewById(R.id.btnSignUp);
        edtEmailSI = findViewById(R.id.edtEmailSI);
        edtPasswordSI = findViewById(R.id.edtPasswordSI);
        edtEmailSIWrapper = findViewById(R.id.edtEmailSIWrapper);
        edtPasswordSIWrapper = findViewById(R.id.edtPasswordSIWrapper);
        tvForgotPass = findViewById(R.id.tvForgotPass);

    }

    private void OnCLick() {
        // click Sign In
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Email = edtEmailSI.getText().toString().trim();
                String Password = edtPasswordSI.getText().toString().trim();

                if (TextUtils.isEmpty(Email)) {
                    edtEmailSIWrapper.setError(getString(R.string.please_enter_email));
                    edtEmailSI.requestFocus();
                }
                if (TextUtils.isEmpty(Password)) {
                    edtPasswordSIWrapper.setError(getString(R.string.please_enter_password));
                    edtEmailSI.requestFocus();
                }

                if (!(TextUtils.isEmpty(Password)) && !(TextUtils.isEmpty(Email))) {
                    ProgressDialog progressDialog = new ProgressDialog(SignIn.this);
                    progressDialog.setMessage(getString(R.string.signing_in));
                    progressDialog.setCanceledOnTouchOutside(true);
                    progressDialog.show();

                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    auth.signInWithEmailAndPassword(Email, Password)
                            .addOnCompleteListener(SignIn.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {

                                        progressDialog.dismiss();

                                        if(auth.getCurrentUser().isEmailVerified()){
                                            Intent intent = new Intent(SignIn.this, MainActivity.class);
                                            startActivity(intent);
                                            finishAffinity();
                                        }
                                        else{
                                            Toast.makeText(SignIn.this, getString(R.string.please_verification_email), Toast.LENGTH_SHORT).show();
                                        }

                                    } else {
                                        progressDialog.dismiss();

                                        showAlertDialogFail(task);
                                    }
                                }
                            });
                }
            }
        });

        //click Forgot password
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toRegister = new Intent(SignIn.this, SignUp.class);
                startActivity(toRegister);
            }
        });

        //click ForgotPassword
        tvForgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignIn.this, ForgotPassword.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void showAlertDialogFail(Task<AuthResult> task) {
        AlertDialog.Builder builder = new AlertDialog.Builder(SignIn.this);
        builder.setTitle(R.string.sign_in_failed);
        builder.setMessage("Error " + task.getException().getMessage());
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

}