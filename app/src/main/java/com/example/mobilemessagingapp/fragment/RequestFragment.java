package com.example.mobilemessagingapp.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobilemessagingapp.models.Contact;
import com.example.mobilemessagingapp.R;
import com.example.mobilemessagingapp.activities.MainActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class RequestFragment extends Fragment {


    public RequestFragment() {
        // Required empty public constructor
    }

    private RecyclerView rcvListRequest;
    private DatabaseReference RequestRef, UserRef, ContactRef;
    private FirebaseAuth auth;
    private String currentUserID;

    private MainActivity mainActivity;


    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_request, container, false);
        mainActivity = (MainActivity) getActivity();

        auth = FirebaseAuth.getInstance();
        currentUserID = auth.getCurrentUser().getUid();
        UserRef = FirebaseDatabase.getInstance().getReference().child("User");
        rcvListRequest = view.findViewById(R.id.rcvListRequest);
        rcvListRequest.setLayoutManager(new LinearLayoutManager(getContext()));

        RequestRef = FirebaseDatabase.getInstance().getReference().child("Request");

        ContactRef = FirebaseDatabase.getInstance().getReference().child("Contact");
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Contact> options = new FirebaseRecyclerOptions.Builder<Contact>()
                .setQuery(RequestRef.child(currentUserID), Contact.class)
                .build();

        FirebaseRecyclerAdapter<Contact, RequestViewHolder> adapter = new FirebaseRecyclerAdapter<Contact, RequestViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull RequestViewHolder holder, int position, @NonNull Contact model) {
//                holder.itemView.findViewById(R.id.btnAcceptRequest).setVisibility(View.VISIBLE);
//                holder.itemView.findViewById(R.id.btnCancelRequest).setVisibility(View.VISIBLE);

                final String list_user_id = getRef(position).getKey();

                DatabaseReference getTypeRef = getRef(position).child("request_type").getRef();

                getTypeRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String type = snapshot.getValue().toString();
                            if (type.equals("received")) {
                                UserRef.child(list_user_id).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.hasChild("Image")) {
                                            final String image = snapshot.child("Image").getValue().toString();

                                            Picasso.get().load(image).placeholder(R.drawable.avatar).into(holder.ivImageContact);
                                        }
                                        final String username = snapshot.child("UserName").getValue().toString();

                                        holder.tvNameContact.setText(username);

                                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                CharSequence option[] = new CharSequence[]{
                                                        getString(R.string.accept), getString(R.string.cancel)
                                                };
                                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                                builder.setTitle(username + getString(R.string.request_friend));

                                                builder.setItems(option, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        if (i == 0) {
                                                            ContactRef.child(currentUserID).child(list_user_id).child("Contact")
                                                                    .setValue("Saved")
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if (task.isSuccessful()) {
                                                                                ContactRef.child(list_user_id).child(currentUserID).child("Contact")
                                                                                        .setValue("Saved")
                                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                            @Override
                                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                                if (task.isSuccessful()) {
                                                                                                    RequestRef.child(currentUserID).child(list_user_id)
                                                                                                            .removeValue()
                                                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                @Override
                                                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                                                    if (task.isSuccessful()) {
                                                                                                                        RequestRef.child(list_user_id).child(currentUserID)
                                                                                                                                .removeValue()
                                                                                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                                    @Override
                                                                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                                                                        if (task.isSuccessful()) {
                                                                                                                                            Toast.makeText(getContext(), getString(R.string.added), Toast.LENGTH_SHORT)
                                                                                                                                                    .show();
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
                                                                        }
                                                                    });
                                                        }
                                                        if (i == 1) {
                                                            RequestRef.child(currentUserID).child(list_user_id)
                                                                    .removeValue()
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if (task.isSuccessful()) {
                                                                                RequestRef.child(list_user_id).child(currentUserID)
                                                                                        .removeValue()
                                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                            @Override
                                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                                if (task.isSuccessful()) {
                                                                                                    Toast.makeText(getContext(), getString(R.string.deleted), Toast.LENGTH_SHORT)
                                                                                                            .show();
                                                                                                }
                                                                                            }
                                                                                        });
                                                                            }
                                                                        }
                                                                    });
                                                        }
                                                    }
                                                });
                                                builder.show();
                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            } else if (type.equals("sent")) {
                                holder.ivImageContact.setVisibility(View.INVISIBLE);
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
            public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact, parent, false);
                RequestViewHolder holder = new RequestViewHolder(view);
                return holder;
            }
        };
        rcvListRequest.setAdapter(adapter);
        adapter.startListening();
    }

    public static class RequestViewHolder extends RecyclerView.ViewHolder {

        TextView tvNameContact;
        ImageView ivImageContact;
//        Button btnAcceptRequest, btnCancelRequest;

        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);

            tvNameContact = itemView.findViewById(R.id.tvNameContact);
            ivImageContact = itemView.findViewById(R.id.ivImageContact);
//            btnAcceptRequest = itemView.findViewById(R.id.btnAcceptRequest);
//            btnCancelRequest = itemView.findViewById(R.id.btnCancelRequest);
        }
    }
}