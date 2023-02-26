package com.example.mobilemessagingapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.mobilemessagingapp.models.Contact;
import com.example.mobilemessagingapp.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class FindFriend extends AppCompatActivity {

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private RecyclerView rcvFindFriend;
    private DatabaseReference databaseReference;
    private ImageButton ibSearch;
    private EditText edtSearch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friend);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle(R.string.find_friend);

        }

        databaseReference = FirebaseDatabase.getInstance().getReference().child("User");
        rcvFindFriend = findViewById(R.id.rcvFindFriend);
        rcvFindFriend.setLayoutManager(new LinearLayoutManager(this));

        edtSearch = findViewById(R.id.edtSearch);
        ibSearch = findViewById(R.id.ibSearch);

    }


    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Contact> options = new FirebaseRecyclerOptions.Builder<Contact>()
                        .setQuery(databaseReference, Contact.class)
                        .build();

        FirebaseRecyclerAdapter<Contact, FindFriendViewHolder> adapter =
                new FirebaseRecyclerAdapter<Contact, FindFriendViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull FindFriendViewHolder holder, @SuppressLint("RecyclerView") final int position, @NonNull Contact model)
                    {
                        holder.tvNameContact.setText(model.getUserName());
                        Picasso.get().load(model.getImage()).placeholder(R.drawable.avatar).into(holder.ivImageContact);

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String visit_uid = getRef(position).getKey();

                                Intent intent = new Intent(FindFriend.this, ProfileContact.class);
                                intent.putExtra("visit_uid", visit_uid);
                                startActivity(intent);
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public FindFriendViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
                    {
                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_contact, viewGroup, false);
                        return new FindFriendViewHolder(view);
                    }
                };

        rcvFindFriend.setAdapter(adapter);
        adapter.startListening();
    }

    public static class FindFriendViewHolder extends RecyclerView.ViewHolder{
        TextView tvNameContact;
        CircleImageView ivImageContact;

        public FindFriendViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNameContact = itemView.findViewById(R.id.tvNameContact);
            ivImageContact = itemView.findViewById(R.id.ivImageContact);
        }
    }

}