package com.example.mobilemessagingapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.mobilemessagingapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.util.HashMap;

public class ChangePasswordActivity extends AppCompatActivity {

    private TextInputEditText edtCurrentPassword_cp, edtNewPassword_cp, edtConfirmNewPassword_cp;
    private TextInputLayout edtCurrentPasswordWrapper, edtNewPasswordWrapper, edtConfirmNewPasswordWrapper;
    private Button btnChangePassword;
    private DatabaseReference databaseReference;
    private String currentUserID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.change_password);
        }


        FirebaseAuth auth = FirebaseAuth.getInstance();
        currentUserID = auth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        mapping();

        OnClick();
    }

    private void OnClick() {
        //change password
        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String currentPass = edtCurrentPassword_cp.getText().toString().trim();
                String newPassword = edtNewPassword_cp.getText().toString().trim();
                String confirmNewPassword = edtConfirmNewPassword_cp.getText().toString().trim();

                if (TextUtils.isEmpty(currentPass)) {
                    edtCurrentPasswordWrapper.setError(getString(R.string.you_must_enter_your_current_password));
                }

                if (TextUtils.isEmpty(newPassword)) {
                    edtNewPasswordWrapper.setError(getString(R.string.you_must_enter_your_new_password));
                }
                if (TextUtils.isEmpty(confirmNewPassword)) {
                    edtConfirmNewPasswordWrapper.setError(getString(R.string.you_must_confirm_new_password));
                }
                if(currentPass.equals(newPassword)){
                    edtNewPasswordWrapper.setError(getString(R.string.new_pasword_must_different_old_password));
                }
                if (!(TextUtils.isEmpty(currentPass)) && !(TextUtils.isEmpty(newPassword)) && !(TextUtils.isEmpty(confirmNewPassword)) && !currentPass.equals(newPassword) ) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    final String email = user.getEmail();

                    AuthCredential credential = EmailAuthProvider.getCredential(email, currentPass);

                    user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {

                                if (newPassword.equals(confirmNewPassword)) {
                                    user.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                update(newPassword);
                                                edtCurrentPassword_cp.setText("");
                                                edtNewPassword_cp.setText("");
                                                edtConfirmNewPassword_cp.setText("");
                                            } else {
                                                Toast.makeText(ChangePasswordActivity.this, getString(R.string.update_failed), Toast.LENGTH_LONG).show();
                                                edtCurrentPassword_cp.setText("");
                                                edtNewPassword_cp.setText("");
                                                edtConfirmNewPassword_cp.setText("");
                                            }
                                        }
                                    });
                                } else {
                                    edtConfirmNewPassword_cp.setError(getString(R.string.new_password_and_confirm_new_password_must_be_the_same));
                                    edtConfirmNewPassword_cp.setText("");
                                }
                            } else {
                                edtCurrentPassword_cp.setError(getString(R.string.current_password_is_wrong));
                                edtCurrentPassword_cp.setText("");
                            }
                        }
                    });
                }
            }
        });
    }

    private void update(String newPassword) {

        HashMap<String, Object> profileMap = new HashMap<>();
        profileMap.put("Password", newPassword);

        databaseReference.child("User").child(currentUserID).updateChildren(profileMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(ChangePasswordActivity.this, getString(R.string.update_successfully), Toast.LENGTH_SHORT)
                                    .show();
                        } else {
                            String message = task.getException().toString();
                            Toast.makeText(ChangePasswordActivity.this, "Error: " + message, Toast.LENGTH_SHORT)
                                    .show();
                        }
                    }
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void mapping() {
        edtCurrentPassword_cp = findViewById(R.id.edtCurrentPassword_cp);
        edtNewPassword_cp = findViewById(R.id.edtNewPassword_cp);
        edtConfirmNewPassword_cp = findViewById(R.id.edtConfirmNewPassword_cp);
        edtCurrentPasswordWrapper = findViewById(R.id.edtCurrentPasswordWrapper);
        edtNewPasswordWrapper = findViewById(R.id.edtNewPasswordWrapper);
        edtConfirmNewPasswordWrapper = findViewById(R.id.edtConfirmNewPasswordWrapper);
        btnChangePassword = findViewById(R.id.btnChangePassword);
    }


}