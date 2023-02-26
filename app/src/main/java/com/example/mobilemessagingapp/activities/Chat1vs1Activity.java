package com.example.mobilemessagingapp.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobilemessagingapp.models.Message;
import com.example.mobilemessagingapp.R;
import com.example.mobilemessagingapp.adapter.MessageAdapter;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class Chat1vs1Activity extends AppCompatActivity {

    private String receiverID;
    private String messageSenderID;

    private TextView tvUserName_custom_chat_bar, tvStatus_custom_chat_bar;
    private CircleImageView ivProfile_custom_chat_bar;

    private ImageButton ibSend, ibAttach;
    private EditText edtEnterMessage;

    private String saveCurrentTime, saveCurrentDate;
    private String checker = "", myUrl = "";
    private Uri fileUri;

    private DatabaseReference RootRef;

    private final List<Message> messageList = new ArrayList<>();
    private MessageAdapter messageAdapter;
    private RecyclerView rcvMessageofUser;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat1vs1);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        messageSenderID = auth.getCurrentUser().getUid();
        RootRef = FirebaseDatabase.getInstance().getReference();

        receiverID = getIntent().getExtras().get("uid").toString();
        String receiverName = getIntent().getExtras().get("username").toString();
        String receiverImage = getIntent().getExtras().get("image").toString();

        ActionBar actionBar = getSupportActionBar();

        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionBarView = layoutInflater.inflate(R.layout.custom_chat_bar, null);
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setTitle("");
            actionBar.setCustomView(actionBarView);
        }
        mapping();

        tvUserName_custom_chat_bar.setText(receiverName);
        Picasso.get().load(receiverImage).placeholder(R.drawable.avatar).into(ivProfile_custom_chat_bar);

        Onclick();
        DisplayLastActivited();

        RootRef.child("Messages").child(messageSenderID).child(receiverID)
                .addChildEventListener(new ChildEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        Message message = snapshot.getValue(Message.class);
                        messageList.add(message);
                        messageAdapter.notifyDataSetChanged();
                        rcvMessageofUser.smoothScrollToPosition(rcvMessageofUser.getAdapter().getItemCount());
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                        Message message = snapshot.getValue(Message.class);
                        messageList.clear();
                        messageAdapter.notifyDataSetChanged();

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void mapping() {
        ivProfile_custom_chat_bar = findViewById(R.id.ivProfile_custom_chat_bar);
        tvUserName_custom_chat_bar = findViewById(R.id.tvUserName_custom_chat_bar);
        tvStatus_custom_chat_bar = findViewById(R.id.tvStatus_custom_chat_bar);

        ibSend = findViewById(R.id.ibSend);
        edtEnterMessage = findViewById(R.id.edtEnterMessage);
        ibAttach = findViewById(R.id.ibAttach);

        messageAdapter = new MessageAdapter(messageList);
        rcvMessageofUser = findViewById(R.id.rcvMessageofUser);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcvMessageofUser.setLayoutManager(linearLayoutManager);
        rcvMessageofUser.setAdapter(messageAdapter);

        progressDialog = new ProgressDialog(this);

    }

    private void DisplayLastActivited() {

        RootRef.child("User").child(receiverID).addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("Status").hasChild("state")) {
                    String state = (Objects.requireNonNull(snapshot.child("Status").child("state").getValue()).toString());
                    String date = (Objects.requireNonNull(snapshot.child("Status").child("date").getValue()).toString());
                    String time = (Objects.requireNonNull(snapshot.child("Status").child("time").getValue()).toString());

                    if (state.equals("online")) {
                        tvStatus_custom_chat_bar.setText(R.string.online);
                    } else if (state.equals("offline")) {
                        tvStatus_custom_chat_bar.setText(getString(R.string.activited_at) + date + " " + time);
                    }
                } else {
                    tvStatus_custom_chat_bar.setText(R.string.offline);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    //menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);
        return true;
    }
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.item1:
                Intent intent = new Intent(Chat1vs1Activity.this, ProfileContact.class);
                intent.putExtra("visit_uid", receiverID);
                startActivity(intent);
                return true;
            case R.id.item2:
                Toast.makeText(Chat1vs1Activity.this, getString(R.string.under_development), Toast.LENGTH_SHORT).show();
                return true;
            case R.id.item3:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.want_delete_chat);

                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteSentMessage();
                    }
                });

                builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                builder.show();
                return true;
        }
        return false;
    }

    private void deleteSentMessage(){
        RootRef.child("Messages").child(messageSenderID).child(receiverID)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(Chat1vs1Activity.this, "Deleted Successfully", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void Onclick() {
        ibAttach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CharSequence[] option = new CharSequence[]{
                        "Image", "PDF file", "Word file"
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(Chat1vs1Activity.this);
                builder.setTitle(R.string.select_the_file);
                builder.setItems(option, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i == 0) {
                            checker = "image";
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            intent.setType("image/*");
                            launchSomeActivity.launch(intent);
                        }
                        if (i == 1) {
                            checker = "pdf";
                            Toast.makeText(Chat1vs1Activity.this, getString(R.string.under_development), Toast.LENGTH_SHORT).show();
                        }
                        if (i == 2) {
                            checker = "docx";
                            Toast.makeText(Chat1vs1Activity.this, getString(R.string.under_development), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                builder.show();
            }
        });

        ibSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendMessage();
            }
        });
    }
    private void getTime(){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd. yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());
        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        saveCurrentTime = currentTime.format(calendar.getTime());
    }

    private void SendMessage() {
        String messageText = edtEnterMessage.getText().toString().trim();
        if (TextUtils.isEmpty(messageText)) {
            Toast.makeText(this, R.string.please_enter_message, Toast.LENGTH_SHORT).show();
        } else {
            //đường dẫn đễ đưa dữ liệu của tin nhắn lên cho người nhận và người gửi
            String messageSenderRef = "Messages/" + messageSenderID + "/" + receiverID;
            String messageReceiverRef = "Messages/" + receiverID + "/" + messageSenderID;
            //tạo key name cho tin nhắn
            DatabaseReference userMessageKeyRef = RootRef.child("Messages").child(messageSenderID)
                    .child(receiverID).push();
            //lấy key name làm id của tin nhắn
            String messagePushID = userMessageKeyRef.getKey();
            getTime();
            Map messageTextBody = new HashMap();

            messageTextBody.put("message", messageText);
            messageTextBody.put("type", "text");
            messageTextBody.put("from", messageSenderID);
            messageTextBody.put("to", receiverID);
            messageTextBody.put("messageID", messagePushID);
            messageTextBody.put("time", saveCurrentTime);
            messageTextBody.put("date", saveCurrentDate);
            //đẩy tin nhắn lên firebase,  nếu key name chưa có thì sẽ tạo, key đã có thì cập nhật và
            // đặt messageTextBody vào nhánh messagePushID làm các nhánh con, các nhánh con là key name của messageTextBody

            Map messsageBodyDetail = new HashMap();
            messsageBodyDetail.put(messageSenderRef + "/" + messagePushID, messageTextBody);
            messsageBodyDetail.put(messageReceiverRef + "/" + messagePushID, messageTextBody);
            //lưu vào firebase
            RootRef.updateChildren(messsageBodyDetail).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
//                    Toast.makeText(Chat1vs1Activity.this, R.string.message_sent_successfully, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Chat1vs1Activity.this, "Error: ", Toast.LENGTH_SHORT).show();
                }
                edtEnterMessage.setText("");
            });
        }
    }

    private final ActivityResultLauncher<Intent> launchSomeActivity = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    // do your operation from here....
                    if (data != null && data.getData() != null) {

                        progressDialog.setTitle(getString(R.string.sending_file));
                        progressDialog.setMessage(getString(R.string.please_wait));
                        progressDialog.setCanceledOnTouchOutside(false);
                        progressDialog.show();

                        fileUri = data.getData();
                        if (!checker.equals("image")) {
                            //send file docx, pdf
                        } else if (checker.equals("image")) {
                            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Image Files");

                            //đường dẫn đễ put dữ liệu tin nhắn cho hai nhánh người gửi và người nhận
                            String messageSenderRef = "Messages/" + messageSenderID + "/" + receiverID;
                            String messageReceiverRef = "Messages/" + receiverID + "/" + messageSenderID;
                            //tạo key name cho tin nhắn
                            DatabaseReference userMessageKeyRef = RootRef.child("Messages").child(messageSenderID)
                                    .child(receiverID).push();
                            //lấy key name làm id của tin nhắn
                            String messagePushID = userMessageKeyRef.getKey();

                            StorageReference filePath = storageReference.child(messagePushID + ".jpg");

                            StorageTask uploadTask = filePath.putFile(fileUri);

                            uploadTask.continueWithTask(new Continuation() {
                                @Override
                                public Object then(@NonNull Task task) throws Exception {
                                    if (!task.isSuccessful()) {
                                        throw task.getException();
                                    }
                                    return filePath.getDownloadUrl();
                                }
                            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if (task.isSuccessful()) {
                                        Uri downloadUri = task.getResult();
                                        myUrl = downloadUri.toString();
                                        getTime();
                                        Map messageTextBody = new HashMap();

                                        messageTextBody.put("message", myUrl);
                                        messageTextBody.put("name", fileUri.getLastPathSegment());
                                        messageTextBody.put("type", checker);
                                        messageTextBody.put("from", messageSenderID);
                                        messageTextBody.put("to", receiverID);
                                        messageTextBody.put("messageID", messagePushID);
                                        messageTextBody.put("time", saveCurrentTime);
                                        messageTextBody.put("date", saveCurrentDate);

                                        Map messsageBodyDetail = new HashMap();
                                        messsageBodyDetail.put(messageSenderRef + "/" + messagePushID, messageTextBody);
                                        messsageBodyDetail.put(messageReceiverRef + "/" + messagePushID, messageTextBody);

                                        RootRef.updateChildren(messsageBodyDetail).addOnCompleteListener(task1 -> {
                                            if (task1.isSuccessful()) {
                                                progressDialog.dismiss();
//                                                Toast.makeText(Chat1vs1Activity.this, R.string.message_sent_successfully, Toast.LENGTH_SHORT).show();
                                            } else {
                                                progressDialog.dismiss();
                                                Toast.makeText(Chat1vs1Activity.this, "Error", Toast.LENGTH_SHORT).show();
                                            }
                                            edtEnterMessage.setText("");
                                        });
                                    }
                                }
                            });
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(this, R.string.nothing_selected, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

}