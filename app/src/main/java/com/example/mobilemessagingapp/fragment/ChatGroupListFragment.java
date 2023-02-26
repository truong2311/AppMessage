package com.example.mobilemessagingapp.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.mobilemessagingapp.R;
import com.example.mobilemessagingapp.activities.ChatGroup;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class ChatGroupListFragment extends Fragment {


    public ChatGroupListFragment() {
        // Required empty public constructor
    }



    private FloatingActionButton fabCreateGroup;
    private ListView lvGroupChat;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> arrayList = new ArrayList<>();

    private DatabaseReference databaseReference;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chatgroup_list, container, false);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("GroupChat");

        mapping(view);
        Retrieve();
        onClick(view);
        return view;
    }
    private void mapping(View view) {
        fabCreateGroup = view.findViewById(R.id.fabCreateGroup);
        lvGroupChat = view.findViewById(R.id.lvGroupChat);
        arrayAdapter = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_list_item_1, arrayList);
        lvGroupChat.setAdapter(arrayAdapter);
    }

    private void Retrieve() {

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Set<String> set = new HashSet<>();
                Iterator iterator = snapshot.getChildren().iterator();

                while (iterator.hasNext()){
                    set.add(((DataSnapshot)iterator.next()).getKey());
                }
                arrayList.clear();
                arrayList.addAll(set);
                arrayAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void onClick(View view) {
        //click floating button
        fabCreateGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext(), R.style.AlertDialog);
                builder.setTitle(R.string.enter_group_name);

                final EditText edtGroupName = new EditText(view.getContext());
                edtGroupName.setHint("");
                builder.setView(edtGroupName);

                builder.setPositiveButton(R.string.create, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String groupName = edtGroupName.getText().toString().trim();

                        if(TextUtils.isEmpty(groupName)){
                            Toast.makeText(view.getContext(), getString(R.string.please_enter_group_name), Toast.LENGTH_SHORT).show();
                        }
                        else{
                            CreateNewGroup(view, groupName);
                        }
                    }
                });
                builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                builder.show();
            }
        });
        //click item listview
        lvGroupChat.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String currentGroupName = adapterView.getItemAtPosition(i).toString();
                Intent intent = new Intent(view.getContext(), ChatGroup.class);
                intent.putExtra("groupName", currentGroupName);
                startActivity(intent);
            }
        });
    }

    private void CreateNewGroup(View view, String groupname) {
        databaseReference.child(groupname).setValue("").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(view.getContext(), groupname + getText(R.string.created_group), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}