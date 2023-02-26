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


public class ContactFragment extends Fragment {


    public ContactFragment() {
        // Required empty public constructor
    }

    private RecyclerView rcvListContact;
    private FloatingActionButton fabNewContact;
    private DatabaseReference ContactRef, UserRef;
    private FirebaseAuth auth;
    private String currentUserID;
    private MainActivity mainActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contact, container, false);

        mapping(view);
        rcvListContact.setLayoutManager(new LinearLayoutManager(getContext()));
        mainActivity = (MainActivity) getContext();
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(mainActivity, DividerItemDecoration.VERTICAL);
        rcvListContact.addItemDecoration(itemDecoration);

        auth = FirebaseAuth.getInstance();
        currentUserID = auth.getCurrentUser().getUid();

        ContactRef = FirebaseDatabase.getInstance().getReference().child("Contact").child(currentUserID);
        UserRef = FirebaseDatabase.getInstance().getReference().child("User");

        onClick(view);

        return view;
    }

    private void mapping(View view) {
        fabNewContact = view.findViewById(R.id.fabNewContact);
        rcvListContact = view.findViewById(R.id.rcvListContact);

    }

    private void onClick(View view) {
        //new contact
        fabNewContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), FindFriend.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Contact>()
                .setQuery(ContactRef, Contact.class)
                .build();

        final FirebaseRecyclerAdapter<Contact, ContactViewHolder> adapter = new FirebaseRecyclerAdapter<Contact, ContactViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ContactViewHolder holder, int position, @NonNull Contact model) {
                final String userID = getRef(position).getKey();
                UserRef.child(userID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {

                            if(snapshot.child("Status").hasChild("state")){
                                String state = (snapshot.child("Status").child("state").getValue().toString());
                                String date = (snapshot.child("Status").child("date").getValue().toString());
                                String time = (snapshot.child("Status").child("time").getValue().toString());

                                if(state.equals("online")){
                                    holder.ivStatusContact.setVisibility(View.VISIBLE);
                                }
                                else if(state.equals("offline")){
                                    holder.ivStatusContact.setVisibility(View.INVISIBLE);
                                }
                            }
                            else{
                                holder.ivStatusContact.setVisibility(View.INVISIBLE);
                            }

                            if (snapshot.hasChild("Image")) {
                                String image = snapshot.child("Image").getValue().toString();
                                String username = snapshot.child("UserName").getValue().toString();

                                holder.tvNameContact.setText(username);
                                Picasso.get().load(image).placeholder(R.drawable.avatar).into(holder.ivImageContact);
                            } else {
                                String username = snapshot.child("UserName").getValue().toString();

                                holder.tvNameContact.setText(username);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @NonNull
            @Override
            public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact, parent, false);
                ContactViewHolder viewHolder = new ContactViewHolder(view);
                return viewHolder;
            }
        };
        rcvListContact.setAdapter(adapter);
        adapter.startListening();
    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder {

        TextView tvNameContact;
        ImageView ivImageContact, ivStatusContact;


        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);

            tvNameContact = itemView.findViewById(R.id.tvNameContact);
            ivImageContact = itemView.findViewById(R.id.ivImageContact);
            ivStatusContact = itemView.findViewById(R.id.ivStatusContact);
        }
    }
}