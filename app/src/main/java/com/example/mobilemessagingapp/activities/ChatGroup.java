package com.example.mobilemessagingapp.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobilemessagingapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;

public class ChatGroup extends AppCompatActivity {


    private ImageButton ibSendMessage;
    private EditText edtSendMessage;
    private ScrollView scrollView;
    private TextView tvDisplay;

    private String currentUserID;
    private String currentUserName;
    private String currentDate;
    private String currentTime;

    private DatabaseReference userRef, groupNameRef, groupMessageKeyRef;

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        String currentGroupName = getIntent().getExtras().get("groupName").toString();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.group) + " " + currentGroupName);
        }

        FirebaseAuth auth = FirebaseAuth.getInstance();
        currentUserID = Objects.requireNonNull(auth.getCurrentUser()).getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("User");
        groupNameRef = FirebaseDatabase.getInstance().getReference().child("GroupChat").child(currentGroupName);

        mapping();

        getUserInfo();

        OnClick();

    }
    private void getTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDateFormat = new SimpleDateFormat("MMM dd, yyyy");
        currentDate = currentDateFormat.format(calendar.getTime());

        SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm a");
        currentTime = currentTimeFormat.format(calendar.getTime());
    }

    private void OnClick() {
        ibSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = edtSendMessage.getText().toString().trim();
                String messageKey = groupNameRef.push().getKey();

                if (TextUtils.isEmpty(message)) {
                    Toast.makeText(ChatGroup.this, R.string.please_enter_message, Toast.LENGTH_SHORT).show();
                } else {
                    getTime();

                    HashMap<String, Object> groupMessageKey = new HashMap<>();
                    groupNameRef.updateChildren(groupMessageKey);

                    if (messageKey != null) {
                        groupMessageKeyRef = groupNameRef.child(messageKey);
                    }

                    HashMap<String, Object> messageInfoMap = new HashMap<>();
                    messageInfoMap.put("UserName", currentUserName);
                    messageInfoMap.put("Message", message);
                    messageInfoMap.put("Date", currentDate);
                    messageInfoMap.put("Time", currentTime);
                    groupMessageKeyRef.updateChildren(messageInfoMap);
                }
                edtSendMessage.setText("");
                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }

    private void getUserInfo() {
        userRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    currentUserName = Objects.requireNonNull(snapshot.child("UserName").getValue()).toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void mapping() {
        ibSendMessage = findViewById(R.id.ibSendMessage);
        edtSendMessage = findViewById(R.id.edtSendMessage);
        scrollView = findViewById(R.id.scrollView);
        tvDisplay = findViewById(R.id.tvDisplay);
    }
    @Override
    protected void onStart() {
        super.onStart();

        groupNameRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot.exists()){
                    DisplayMessage(snapshot);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot.exists()){
                    DisplayMessage(snapshot);
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    private void DisplayMessage(DataSnapshot snapshot) {
        Iterator iterator = snapshot.getChildren().iterator();

        while (iterator.hasNext()){
            String chatDate = (String) ((DataSnapshot) iterator.next()).getValue();
            String chatMessage = (String) ((DataSnapshot) iterator.next()).getValue();
            String chatTime = (String) ((DataSnapshot) iterator.next()).getValue();
            String chatUserName = (String) ((DataSnapshot) iterator.next()).getValue();

            tvDisplay.append(chatUserName+ ": \n"+ chatMessage + "\n" + chatTime
            + "      " + chatDate + "\n\n\n");

            scrollView.fullScroll(ScrollView.FOCUS_DOWN);
        }
    }
}