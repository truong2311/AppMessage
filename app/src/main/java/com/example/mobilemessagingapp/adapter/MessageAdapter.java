package com.example.mobilemessagingapp.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobilemessagingapp.R;
import com.example.mobilemessagingapp.models.Message;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    private List<Message> messageList;
    private FirebaseAuth auth;
    private DatabaseReference UserRef;

    public MessageAdapter(List<Message> messageList) {
        this.messageList = messageList;
    }


    public class MessageViewHolder extends RecyclerView.ViewHolder {
        public TextView tvSenderMessage, tvReceiverMessage, tvdateReceiverMessageText, tvdateSenderMessageText,
                tvdateSenderMessageImage, tvdateReceiverMessageImage;
        public CircleImageView ivReceiverProfileImageMessage;
        public ImageView ivSenderMessage, ivReceiverMessage;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);

            tvSenderMessage = itemView.findViewById(R.id.tvSenderMessage);
            tvReceiverMessage = itemView.findViewById(R.id.tvReceiverMessage);

            tvdateSenderMessageText = itemView.findViewById(R.id.tvdateSenderMessageText);
            tvdateReceiverMessageText = itemView.findViewById(R.id.tvdateReceiverMessageText);
            tvdateSenderMessageImage = itemView.findViewById(R.id.tvdateSenderMessageImage);
            tvdateReceiverMessageImage = itemView.findViewById(R.id.tvdateReceiverMessageImage);

            ivSenderMessage = itemView.findViewById(R.id.ivSenderMessage);
            ivReceiverMessage = itemView.findViewById(R.id.ivReceiverMessage);
            ivReceiverProfileImageMessage = itemView.findViewById(R.id.ivReceiverProfileImageMessage);
        }
    }

    @NonNull
    @Override
    public MessageAdapter.MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_message_layout, parent, false);
        auth = FirebaseAuth.getInstance();
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.MessageViewHolder holder, int position) {
        String SenderId = auth.getCurrentUser().getUid();
        Message message = messageList.get(position);

        String fromUid = message.getFrom();
        String fromMessageType = message.getType();

        UserRef = FirebaseDatabase.getInstance().getReference().child("User").child(fromUid);

        UserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild("Image")) {
                    String receiverImage = snapshot.child("Image").getValue().toString();

                    Picasso.get().load(receiverImage).placeholder(R.drawable.avatar).into(holder.ivReceiverProfileImageMessage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.tvReceiverMessage.setVisibility(View.GONE);
        holder.ivReceiverProfileImageMessage.setVisibility(View.GONE);
        holder.tvSenderMessage.setVisibility(View.GONE);
        holder.ivSenderMessage.setVisibility(View.GONE);
        holder.ivReceiverMessage.setVisibility(View.GONE);
        holder.tvdateReceiverMessageText.setVisibility(View.GONE);
        holder.tvdateSenderMessageText.setVisibility(View.GONE);
        holder.tvdateSenderMessageImage.setVisibility(View.GONE);
        holder.tvdateReceiverMessageImage.setVisibility(View.GONE);

        if (fromMessageType.equals("text")) {

            if (fromUid.equals(SenderId)) {
                holder.tvSenderMessage.setVisibility(View.VISIBLE);
                holder.tvdateSenderMessageText.setVisibility(View.VISIBLE);

                holder.tvSenderMessage.setBackgroundResource(R.drawable.sender_message_layout);
                holder.tvSenderMessage.setTextColor(Color.WHITE);
                holder.tvSenderMessage.setText(message.getMessage());
                holder.tvdateSenderMessageText.setText(message.getTime() + " - " + message.getDate());
            } else {
                holder.ivReceiverProfileImageMessage.setVisibility(View.VISIBLE);
                holder.tvReceiverMessage.setVisibility(View.VISIBLE);
                holder.tvdateReceiverMessageText.setVisibility(View.VISIBLE);

                holder.tvReceiverMessage.setBackgroundResource(R.drawable.receiver_message_layout);
                holder.tvReceiverMessage.setTextColor(Color.BLACK);
                holder.tvReceiverMessage.setText(message.getMessage());
                holder.tvdateReceiverMessageText.setText(message.getTime() + " - " + message.getDate());
            }
        }
        if (fromMessageType.equals("image")) {
            if (fromUid.equals(SenderId)) {
                holder.ivSenderMessage.setVisibility(View.VISIBLE);
                holder.tvdateSenderMessageImage.setVisibility(View.VISIBLE);

                Picasso.get().load(message.getMessage()).into(holder.ivSenderMessage);
                holder.tvdateSenderMessageImage.setText(message.getTime() + " - " + message.getDate());
            } else {
                holder.ivReceiverProfileImageMessage.setVisibility(View.VISIBLE);
                holder.ivReceiverMessage.setVisibility(View.VISIBLE);
                holder.tvdateReceiverMessageImage.setVisibility(View.VISIBLE);

                Picasso.get().load(message.getMessage()).into(holder.ivReceiverMessage);
                holder.tvdateReceiverMessageImage.setText(message.getTime() + " - " + message.getDate());
            }
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }
}
