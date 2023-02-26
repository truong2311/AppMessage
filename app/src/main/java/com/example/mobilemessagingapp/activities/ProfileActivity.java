package com.example.mobilemessagingapp.activities;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;

import android.graphics.Bitmap;
import android.net.Uri;

import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;

import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;


import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mobilemessagingapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;


public class ProfileActivity extends AppCompatActivity {

    private ImageView ivUserImageP;
    private EditText edtUserNameP, edtDateofBirthP, edtAddressP;
    private Button btnUpdateInformationP, btnSelectDate;
    private RadioButton rbtnMale, rbtnFemale;
    private RadioGroup grouprbtnGender;

    private String currentUserID;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.profile);
        }


        FirebaseAuth auth = FirebaseAuth.getInstance();
        currentUserID = auth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference().child("Profile Image");

        requestPermission();
        mapping();
        getinformation();
        onclick();
    }

    private void mapping() {
        ivUserImageP = findViewById(R.id.ivUserImageP);
        edtUserNameP = findViewById(R.id.edtUserNameP);
        edtDateofBirthP = findViewById(R.id.edtDateofBirthP);
        btnSelectDate = findViewById(R.id.btnSelectDate);
        rbtnMale = findViewById(R.id.rbtnMale);
        rbtnFemale = findViewById(R.id.rbtnFemale);
        edtAddressP = findViewById(R.id.edtAddressP);
        btnUpdateInformationP = findViewById(R.id.btnUpdateInformationP);
        grouprbtnGender = findViewById(R.id.grouprbtnGender);

    }

    private void getinformation() {
        databaseReference.child("User").child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if ((snapshot.exists()) && (snapshot.hasChild("UserName"))
                        && (snapshot.hasChild("Image"))) {

                    String getimage = snapshot.child("Image").getValue().toString();
                    String getname = snapshot.child("UserName").getValue().toString();
                    String getdateofbirth = snapshot.child("DateOfBirth").getValue().toString();
                    String getGender = snapshot.child("Gender").getValue().toString();
                    String getAddress = snapshot.child("Address").getValue().toString();

                    Picasso.get().load(getimage).into(ivUserImageP);
                    edtUserNameP.setText(getname);
                    edtDateofBirthP.setText(getdateofbirth);

                    // Check which radio button was clicked
                    switch (getGender) {
                        case "Male":
                            rbtnMale.setChecked(true);
                            break;
                        case "Female":
                            rbtnFemale.setChecked(true);
                            break;
                    }

                    edtAddressP.setText(getAddress);

                } else if ((snapshot.exists()) && (snapshot.hasChild("UserName"))) {

                    String getname = snapshot.child("UserName").getValue().toString();
                    String getdateofbirth = snapshot.child("DateOfBirth").getValue().toString();
                    String getGender = snapshot.child("Gender").getValue().toString();
                    String getAddress = snapshot.child("Address").getValue().toString();

                    edtUserNameP.setText(getname);
                    edtDateofBirthP.setText(getdateofbirth);

                    switch (getGender) {
                        case "Male":
                            rbtnMale.setChecked(true);
                            break;
                        case "Female":
                            rbtnFemale.setChecked(true);
                            break;
                    }

                    edtAddressP.setText(getAddress);
                } else {
                    Toast.makeText(ProfileActivity.this, R.string.please_update_profile, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    @SuppressLint("NonConstantResourceId")
    private void onclick() {

        //chọn ảnh
        ivUserImageP.setOnClickListener(view -> {
            imageChooser();
        });
        //chọn ngày
        btnSelectDate.setOnClickListener(view -> getDate());
        //cập nhật thông tin
        btnUpdateInformationP.setOnClickListener(view -> {

            String setUsername = edtUserNameP.getText().toString().trim();

            String setDateofBirth = edtDateofBirthP.getText().toString().trim();

            String setGender = "";

            int idChecked = grouprbtnGender.getCheckedRadioButtonId();
            switch (idChecked) {
                case R.id.rbtnMale:
                    setGender = "Male";
                    break;
                case R.id.rbtnFemale:
                    setGender = "Female";
                    break;
            }

            String setAddress = edtAddressP.getText().toString().trim();

            if (TextUtils.isEmpty(setUsername)) {
                Toast.makeText(ProfileActivity.this, R.string.please_enter_username, Toast.LENGTH_SHORT).show();
            }
            if (TextUtils.isEmpty(setDateofBirth)) {
                Toast.makeText(ProfileActivity.this, R.string.please_enter_date_of_birth, Toast.LENGTH_SHORT).show();
            }
            if (TextUtils.isEmpty(setAddress)) {
                Toast.makeText(ProfileActivity.this, R.string.please_enter_address, Toast.LENGTH_SHORT).show();
            }
            if (!TextUtils.isEmpty(setUsername) && !TextUtils.isEmpty(setDateofBirth)
                    && !TextUtils.isEmpty(setGender) && !TextUtils.isEmpty(setAddress)) {

                updatedata(setUsername, setDateofBirth, setGender, setAddress);

            }
        });
    }

    private void updatedata(String setUsername, String setDateofBirth, String setGender, String setAddress) {
        HashMap<String, Object> profileMap = new HashMap<>();

        profileMap.put("uid", currentUserID);
        profileMap.put("UserName", setUsername);
        profileMap.put("DateOfBirth", setDateofBirth);
        profileMap.put("Gender", setGender);
        profileMap.put("Address", setAddress);

        databaseReference.child("User").child(currentUserID).updateChildren(profileMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(ProfileActivity.this, R.string.update_profile_successfully, Toast.LENGTH_LONG)
                                    .show();
                            Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            String message = task.getException().toString();
                            Toast.makeText(ProfileActivity.this, "Error: " + message, Toast.LENGTH_LONG)
                                    .show();
                        }
                    }
                });
    }

    private void getDate() {
        int selectedYear = 2000;
        int selectedMonth = 1;
        int selectedDayOfMonth = 1;

        // Date Select Listener.
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {

            @SuppressLint("SetTextI18n")
            @Override
            public void onDateSet(DatePicker view, int year,
                                  int monthOfYear, int dayOfMonth) {

                edtDateofBirthP.setText(dayOfMonth + " - " + (monthOfYear + 1) + " - " + year);
            }
        };
        // Create DatePickerDialog (Spinner Mode):
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                dateSetListener, selectedYear, selectedMonth, selectedDayOfMonth);
        // Show
        datePickerDialog.show();
    }

    private void imageChooser() {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);

        launchSomeActivity.launch(i);
    }

    private final ActivityResultLauncher<Intent> launchSomeActivity = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    // do your operation from here....
                    if (data != null && data.getData() != null) {
                        Uri selectedImageUri = data.getData();
                        Bitmap selectedImageBitmap = null;
                        try {
                            selectedImageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        ivUserImageP.setImageBitmap(selectedImageBitmap);

                        ProgressDialog progressDialog = new ProgressDialog(this);
                        progressDialog.setTitle(getString(R.string.set_profile_image));
                        progressDialog.setMessage(getString(R.string.please_wait));
                        progressDialog.setCanceledOnTouchOutside(false);
                        progressDialog.show();

                        StorageReference filePatch = storageReference.child(currentUserID + ".jpg");
                        filePatch.putFile(selectedImageUri)
                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        final Task<Uri> firebaseUri = taskSnapshot.getStorage().getDownloadUrl();
                                        firebaseUri.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                final String downloadUrl = uri.toString();

                                                databaseReference.child("User").child(currentUserID).child("Image")
                                                        .setValue(downloadUrl)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    Toast.makeText(ProfileActivity.this, getString(R.string.upload_successfully), Toast.LENGTH_SHORT).show();
                                                                    progressDialog.dismiss();
                                                                } else {
                                                                    Toast.makeText(ProfileActivity.this, "Error: " + task.getException().toString(), Toast.LENGTH_SHORT).show();
                                                                    progressDialog.dismiss();
                                                                }
                                                            }
                                                        });
                                            }
                                        });
                                    }
                                });
//                        filePatch.putFile(selectedImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
//                            @Override
//                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
//                                if(task.isSuccessful()){
//                                    Toast.makeText(ProfileActivity.this, "Upload successfully", Toast.LENGTH_SHORT).show();
//                                    final String downloadUri = u
//                                }
//                                else
//                                    Toast.makeText(ProfileActivity.this, "Upload failed", Toast.LENGTH_SHORT).show();
//                            }
//                        });
                    }
                }
            });

    private void requestPermission(){
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
            }
        };
        TedPermission.create()
                .setPermissionListener(permissionlistener)
                .setDeniedMessage(R.string.permission)
                .setPermissions(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE)
                .check();
    }

}

