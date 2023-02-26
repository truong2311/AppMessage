package com.example.mobilemessagingapp.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mobilemessagingapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileContact extends AppCompatActivity {

    private String uidContact, currentUid, CurrentState;

    private CircleImageView ivContactImage;
    private TextView tvContactUserName, tvContactEmail, tvContactDateofBirth, tvContactGender, tvContactAddress;
    private Button btnAddFriend, btnCancelFriend;
    private DatabaseReference userRef, RequestRef, ContactRef;

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_contact);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        FirebaseAuth auth = FirebaseAuth.getInstance();
        currentUid = auth.getCurrentUser().getUid();

        userRef = FirebaseDatabase.getInstance().getReference().child("User");
        RequestRef = FirebaseDatabase.getInstance().getReference().child("Request");
        ContactRef = FirebaseDatabase.getInstance().getReference().child("Contact");

        uidContact = getIntent().getExtras().get("visit_uid").toString();

        mapping();
        CurrentState = "new";

        RetriveInfor();
    }

    private void mapping() {
        ivContactImage = findViewById(R.id.ivContactImage);
        tvContactUserName = findViewById(R.id.tvContactUserName);
        tvContactEmail = findViewById(R.id.tvContactEmail);
        tvContactDateofBirth = findViewById(R.id.tvContactDateofBirth);
        tvContactGender = findViewById(R.id.tvContactGender);
        tvContactAddress = findViewById(R.id.tvContactAddress);
        btnAddFriend = findViewById(R.id.btnAddFriend);
        btnCancelFriend = findViewById(R.id.btnCancelFriend);
    }

    private void RetriveInfor() {
        userRef.child(uidContact).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if ((snapshot.exists()) && (snapshot.hasChild("Image"))) {
                    String Image = snapshot.child("Image").getValue().toString();
                    String UserName = snapshot.child("UserName").getValue().toString();
                    String Email = snapshot.child("Email").getValue().toString();
                    String Gender = snapshot.child("Gender").getValue().toString();
                    String DateOfBirth = snapshot.child("DateOfBirth").getValue().toString();
                    String Address = snapshot.child("Address").getValue().toString();

                    Picasso.get().load(Image).placeholder(R.drawable.avatar).into(ivContactImage);
                    tvContactUserName.setText(UserName);
                    tvContactEmail.setText(Email);
                    tvContactDateofBirth.setText(DateOfBirth);
                    tvContactGender.setText(Gender);
                    tvContactAddress.setText(Address);

                    ManageRequest();
                }
                else {
                    String UserName = snapshot.child("UserName").getValue().toString();
                    String Email = snapshot.child("Email").getValue().toString();
                    String Gender = snapshot.child("Gender").getValue().toString();
                    String DateOfBirth = snapshot.child("DateOfBirth").getValue().toString();
                    String Address = snapshot.child("Address").getValue().toString();

                    tvContactUserName.setText(UserName);
                    tvContactEmail.setText(Email);
                    tvContactDateofBirth.setText(DateOfBirth);
                    tvContactGender.setText(Gender);
                    tvContactAddress.setText(Address);

                    ManageRequest();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void ManageRequest() {
        RequestRef.child(currentUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //kiểm tra người dùng có id của đối phương
                if (snapshot.hasChild(uidContact)) {
                    String request_type = snapshot.child(uidContact).child("request_type")
                            .getValue().toString();

                    if(request_type.equals("sent")){
                        CurrentState = "request_sent";
                        btnAddFriend.setText(R.string.cancel_add_friend);
                        btnAddFriend.setBackgroundColor(Color.RED);
                    }
                    else if(request_type.equals("received")){
                        CurrentState = "request_received";
                        btnAddFriend.setText(R.string.accept_friend);

                        btnCancelFriend.setVisibility(View.VISIBLE);
                        btnCancelFriend.setEnabled(true);
                        btnCancelFriend.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                CancelRequest();
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        ContactRef.child(currentUid).child(uidContact).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    CurrentState = "friend";
                    btnAddFriend.setText(R.string.unfriend);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        if (!currentUid.equals(uidContact)) {
            btnAddFriend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    btnAddFriend.setEnabled((false));
                    if (CurrentState.equals("new")) {
                        SendRequest();
                    }
                    if(CurrentState.equals("request_sent")){
                        CancelRequest();
                    }
                    if(CurrentState.equals("request_received")){
                        AcceptRequest();
                    }
                    if(CurrentState.equals("friend")){
                        Unfriend();
                    }
                }
            });
        } else {
            btnAddFriend.setVisibility(View.INVISIBLE);
        }
    }

    private void Unfriend() {
        ContactRef.child(currentUid).child(uidContact)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            ContactRef.child(uidContact).child(currentUid)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                btnAddFriend.setEnabled(true);
                                                CurrentState = "new";
                                                btnAddFriend.setBackground(getDrawable(R.drawable.button));
                                                btnAddFriend.setText(R.string.add_friend);

                                                btnCancelFriend.setVisibility(View.INVISIBLE);
                                                btnCancelFriend.setEnabled(false);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void AcceptRequest() {
        ContactRef.child(currentUid).child(uidContact).child("Contact").setValue("Saved")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            ContactRef.child(uidContact).child(currentUid).child("Contact").setValue("Saved")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                RequestRef.child(currentUid).child(uidContact)
                                                        .removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if(task.isSuccessful()){
                                                                    RequestRef.child(uidContact).child(currentUid)
                                                                            .removeValue()
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    btnAddFriend.setEnabled(true);
                                                                                    CurrentState="friend";
                                                                                    btnAddFriend.setText(R.string.unfriend);

                                                                                    btnCancelFriend.setVisibility(View.INVISIBLE);
                                                                                    btnCancelFriend.setEnabled(false);
                                                                                }
                                                                            });
                                                                }
                                                            }
                                                        });
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void CancelRequest() {
        RequestRef.child(currentUid).child(uidContact)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            RequestRef.child(uidContact).child(currentUid)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                btnAddFriend.setEnabled(true);
                                                CurrentState = "new";
                                                btnAddFriend.setBackground(getDrawable(R.drawable.button));
                                                btnAddFriend.setText(R.string.add_friend);

                                                btnCancelFriend.setVisibility(View.INVISIBLE);
                                                btnCancelFriend.setEnabled(false);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void SendRequest() {
        RequestRef.child(currentUid).child(uidContact).child("request_type").setValue("sent")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            RequestRef.child(uidContact).child(currentUid).child("request_type")
                                    .setValue("received").addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {

                                                btnAddFriend.setEnabled(true);
                                                CurrentState = "request_sent";
                                                btnAddFriend.setBackground(getDrawable(R.drawable.button_red));
                                                btnAddFriend.setText(R.string.cancel_add_friend);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

}