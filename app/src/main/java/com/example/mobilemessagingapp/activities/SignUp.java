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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobilemessagingapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class SignUp extends AppCompatActivity {

    //khai báo
    private TextInputEditText edtEmailR, edtPasswordR, edtConfirmPasswordR;
    private TextInputLayout edtEmailRWrapper, edtPasswordRWrapper, edtConfirmPasswordRWrapper;
    private Button btnSignUp;
    private TextView tvDoYouhaveAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mapping();

        OnClick();
    }

    private void OnClick() {
        // đăng ký
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String Email = edtEmailR.getText().toString().trim();
                String Password = edtPasswordR.getText().toString().trim();
                String ConfirmPassword = edtConfirmPasswordR.getText().toString().trim();

                //kiểm tra dl đầu vào trống
                if (TextUtils.isEmpty(Email)) {
                    edtEmailRWrapper.setError(getString(R.string.please_enter_email));
                }
                if (TextUtils.isEmpty(Password)) {
                    edtPasswordRWrapper.setError(getString(R.string.please_enter_password));
                }
                if (TextUtils.isEmpty(Password)) {
                    edtConfirmPasswordRWrapper.setError(getString(R.string.please_enter_confirm_password));
                }
                if (!TextUtils.isEmpty(Email) && !TextUtils.isEmpty(Password)
                        && !TextUtils.isEmpty(Password)) {
                    if (!(ConfirmPassword.equals(Password))) {
                        edtConfirmPasswordRWrapper.setError(getString(R.string.confirm_same_pass));
                        edtConfirmPasswordR.setText("");
                        edtConfirmPasswordR.requestFocus();
                    } else {
                        signup(Email, Password);
                        clear();
                    }
                }
            }
        });
        //chuyển sang sign in
        tvDoYouhaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUp.this, SignIn.class);
                startActivity(intent);
                finish();
            }
        });
    }

    //ánh xạ
    private void mapping() {
        edtEmailR = findViewById(R.id.edtEmailR);
        edtPasswordR = findViewById(R.id.edtPasswordR);
        edtConfirmPasswordR = findViewById(R.id.edtConfirmPasswordR);
        edtEmailRWrapper = findViewById(R.id.edtEmailRWrapper);
        edtPasswordRWrapper = findViewById(R.id.edtPasswordRWrapper);
        edtConfirmPasswordRWrapper = findViewById(R.id.edtConfirmPasswordRWrapper);
        btnSignUp = findViewById(R.id.btnSignUp);
        tvDoYouhaveAccount = findViewById(R.id.tvDoYouhaveAccount);
    }
    //đăng ký
    private void signup(String Email, String Password){
        ProgressDialog progressDialog = new ProgressDialog(SignUp.this);
        progressDialog.setMessage(getString(R.string.creating_new_account));
        progressDialog.setCanceledOnTouchOutside(true);
        progressDialog.show();

        //đăng ký
        FirebaseAuth auth = FirebaseAuth.getInstance();

        auth.createUserWithEmailAndPassword(Email, Password)
                .addOnCompleteListener(SignUp.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressDialog.dismiss();

                            DatabaseReference myref = FirebaseDatabase.getInstance().getReference();

                            String currentUserID = auth.getCurrentUser().getUid();
                            myref.child("User").child(currentUserID).setValue("");

                            HashMap<String, String> profileMap = new HashMap<>();

                            profileMap.put("uid", currentUserID);
                            profileMap.put("Email", Email);
                            profileMap.put("Password", Password);

                            myref.child("User").child(currentUserID).setValue(profileMap);

                            showAlertDiaLogSuccess();

                            VerificationEmail(auth);

                            FirebaseAuth.getInstance().signOut();
                        } else {
                            progressDialog.dismiss();

                            showAlertDiaLogFail(task);
                        }
                    }
                });
    }

    private void showAlertDiaLogSuccess(){
        AlertDialog.Builder builder = new AlertDialog.Builder(SignUp.this);
        builder.setTitle(R.string.sign_up_success);
        builder.setMessage(R.string.do_you_want_go_to_sign_in);

        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.show();
    }
    private void showAlertDiaLogFail(Task<AuthResult> task){
        AlertDialog.Builder builder = new AlertDialog.Builder(SignUp.this);
        builder.setTitle(R.string.sign_up_failed);
        builder.setMessage("Error: " + task.getException().getMessage());
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }
    private void VerificationEmail(FirebaseAuth auth){
        FirebaseUser user = auth.getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(SignUp.this, getString(R.string.check_your_email), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void clear(){
        edtEmailR.setText("");
        edtPasswordR.setText("");
        edtConfirmPasswordR.setText("");
        edtEmailR.requestFocus();
    }
}