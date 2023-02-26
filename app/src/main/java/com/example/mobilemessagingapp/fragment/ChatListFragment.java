package com.example.mobilemessagingapp.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mobilemessagingapp.models.Contact;
import com.example.mobilemessagingapp.R;
import com.example.mobilemessagingapp.activities.Chat1vs1Activity;
import com.example.mobilemessagingapp.activities.FindFriend;
import com.example.mobilemessagingapp.activities.MainActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


public class ChatListFragment extends Fragment {


    public ChatListFragment() {
        // Required empty public constructor
    }


    private RecyclerView rcvListChat;
    private DatabaseReference ChatListRef, UserRef;
    private FloatingActionButton fabNewContactCL;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_chat_list, container, false);

        rcvListChat = view.findViewById(R.id.rcvListChat);
        rcvListChat.setLayoutManager(new LinearLayoutManager(getContext()));
        MainActivity mainActivity = (MainActivity) getContext();
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(mainActivity, DividerItemDecoration.VERTICAL);
        rcvListChat.addItemDecoration(itemDecoration);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        String currentUserID = auth.getCurrentUser().getUid();
        ChatListRef = FirebaseDatabase.getInstance().getReference().child("Contact").child(currentUserID);
        UserRef = FirebaseDatabase.getInstance().getReference().child("User");

        fabNewContactCL = view.findViewById(R.id.fabNewContactCL);
        fabNewContactCL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), FindFriend.class);
                startActivity(intent);
            }
        });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Contact> options = new FirebaseRecyclerOptions.Builder<Contact>()
                .setQuery(ChatListRef, Contact.class)
                .build();

        FirebaseRecyclerAdapter<Contact, ChatListViewHolder> adapter = new FirebaseRecyclerAdapter<Contact, ChatListViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ChatListViewHolder holder, int position, @NonNull Contact model) {
                final String userID = getRef(position).getKey();
                final String[] Image = {"default"};

                UserRef.child(userID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            //load ảnh đại diện
                            if(snapshot.hasChild("Image")){
                                Image[0] = snapshot.child("Image").getValue().toString();
                                Picasso.get().load(Image[0]).into(holder.ivImageContact);
                            }
                            //load tên người dùng
                            final  String Username =  snapshot.child("UserName").getValue().toString();
                            holder.tvNameContact.setText(Username);
                            //load trạng thái hoạt động
                            if(snapshot.child("Status").hasChild("state")){
                                String state = (snapshot.child("Status").child("state").getValue().toString());
                                String date = (snapshot.child("Status").child("date").getValue().toString());
                                String time = (snapshot.child("Status").child("time").getValue().toString());

                                if(state.equals("online")){
                                    holder.tvActive.setText(R.string.online);
                                }
                                else if(state.equals("offline")){
                                    holder.tvActive.setText(getString(R.string.activited_at) + date + " " + time);
                                }
                            }
                            else{
                                holder.tvActive.setText(R.string.offline);
                            }

                            holder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(getContext(), Chat1vs1Activity.class);
                                    intent.putExtra("uid", userID);
                                    intent.putExtra("username", Username);
                                    intent.putExtra("image", Image[0]);
                                    startActivity(intent);
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @NonNull
            @Override
            public ChatListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact, parent, false);
                return new ChatListViewHolder(view);
            }
        };
        rcvListChat.setAdapter(adapter);
        adapter.startListening();
    }
    public static class ChatListViewHolder extends RecyclerView.ViewHolder{

        ImageView ivImageContact;
        TextView tvNameContact, tvActive;

        public ChatListViewHolder(@NonNull View itemView) {
            super(itemView);

            ivImageContact = itemView.findViewById(R.id.ivImageContact);
            tvNameContact = itemView.findViewById(R.id.tvNameContact);
            tvActive = itemView.findViewById(R.id.tvActive);
        }
    }
}