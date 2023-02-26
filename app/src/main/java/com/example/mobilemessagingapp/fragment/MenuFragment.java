package com.example.mobilemessagingapp.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobilemessagingapp.R;
import com.example.mobilemessagingapp.activities.ChangePasswordActivity;
import com.example.mobilemessagingapp.activities.LanguageActivity;
import com.example.mobilemessagingapp.activities.MainActivity;
import com.example.mobilemessagingapp.activities.ProfileActivity;
import com.example.mobilemessagingapp.activities.SignIn;
import com.example.mobilemessagingapp.adapter.itemMenuAdapter;
import com.example.mobilemessagingapp.models.itemMenu;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class MenuFragment extends Fragment {


    public MenuFragment() {
        // Required empty public constructor
    }
    private MainActivity mainActivity;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_menu, container, false);
        getinfo(view);

        mainActivity = (MainActivity) getActivity();

        //personal menu
        RecyclerView recyclerView1 = view.findViewById(R.id.recyclerView_PersonalMenu);
        LinearLayoutManager linearLayout = new LinearLayoutManager(mainActivity);
        recyclerView1.setLayoutManager(linearLayout);

        itemMenuAdapter itemMenuAdapter1 = new itemMenuAdapter(menuperson(), new itemMenuAdapter.IOnClick() {
            @Override
            public void OnClick(itemMenu itemMenu) {
                if(itemMenu.getIditem() == 1){
                    Intent intent = new Intent(mainActivity, ProfileActivity.class);
                    startActivity(intent);
                }
                if(itemMenu.getIditem() == 2){
                    Intent intent = new Intent(mainActivity, ChangePasswordActivity.class);
                    startActivity(intent);
                }
                if(itemMenu.getIditem() == 3){
                    signOutAcc(view);
                }
            }
        });
        recyclerView1.setAdapter(itemMenuAdapter1);
        //RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(mainActivity, DividerItemDecoration.VERTICAL);
        //recyclerView1.addItemDecoration(itemDecoration);

        //personal menu
        RecyclerView recyclerView2 = view.findViewById(R.id.recyclerView_SetingMenu);
        LinearLayoutManager linearLayout2 = new LinearLayoutManager(mainActivity);
        recyclerView2.setLayoutManager(linearLayout2);

        itemMenuAdapter itemMenuAdapter2 = new itemMenuAdapter(menusetting(), new itemMenuAdapter.IOnClick() {
            @Override
            public void OnClick(itemMenu itemMenu) {
                if(itemMenu.getIditem() == 4){
                    Toast.makeText(mainActivity, R.string.under_development, Toast.LENGTH_SHORT).show();
                }
                if(itemMenu.getIditem() == 5){
                    Intent intent = new Intent(mainActivity, LanguageActivity.class);
                    startActivity(intent);
                }
                if(itemMenu.getIditem() == 6){
                    Toast.makeText(mainActivity, R.string.under_development, Toast.LENGTH_SHORT).show();
                }
            }
        });
        recyclerView2.setAdapter(itemMenuAdapter2);
        return view;
    }

    private ImageView ivUserImage;
    private TextView tvUserName;
    private TextView tvUserEmail;

    private void getinfo(View view) {
        ivUserImage = view.findViewById(R.id.ivUserImage);
        tvUserName = view.findViewById(R.id.tvUserName);
        tvUserEmail = view.findViewById(R.id.tvUserEmail);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        String currentUserID = auth.getCurrentUser().getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();


        databaseReference.child("User").child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if ((snapshot.exists()) && (snapshot.hasChild("UserName"))
                        && (snapshot.hasChild("Image"))) {

                    String getimage = snapshot.child("Image").getValue().toString();
                    String getname = snapshot.child("UserName").getValue().toString();
                    String getEmail = snapshot.child("Email").getValue().toString();

                    Picasso.get().load(getimage).placeholder(R.drawable.avatar).into(ivUserImage);
                    tvUserName.setText(getname);
                    tvUserEmail.setText(getEmail);


                } else if ((snapshot.exists()) && (snapshot.hasChild("UserName"))) {

                    String getname = snapshot.child("UserName").getValue().toString();
                    String getEmail = snapshot.child("Email").getValue().toString();

                    tvUserName.setText(getname);
                    tvUserEmail.setText(getEmail);
                }
                else {
                    Toast.makeText(view.getContext(), getString(R.string.please_update_profile), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }

    //create menu person
    private List<itemMenu> menuperson() {
        List<itemMenu> itemMenuList = new ArrayList<>();
        itemMenuList.add(new itemMenu(1, R.drawable.icon_profile, getString(R.string.my_profile)));
        itemMenuList.add(new itemMenu(2, R.drawable.icon_password, getString(R.string.change_password)));
        itemMenuList.add(new itemMenu(3, R.drawable.icon_signout, getString(R.string.sign_out)));
        return itemMenuList;
    }
    //create menu setting
    private List<itemMenu> menusetting() {
        List<itemMenu> itemMenuList = new ArrayList<>();
        itemMenuList.add(new itemMenu(4, R.drawable.icon_notification, getString(R.string.notifications_and_sound)));
        itemMenuList.add(new itemMenu(5, R.drawable.icon_language, getString(R.string.language)));
        itemMenuList.add(new itemMenu(6, R.drawable.icon_theme, getString(R.string.theme)));
        return itemMenuList;
    }
    private void signOutAcc(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setTitle(R.string.are_you_sure);
        builder.setMessage(R.string.do_you_want_sign_out);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                updateUserStatus("offline");
                FirebaseAuth.getInstance().signOut();
                Intent intent3 = new Intent(view.getContext(), SignIn.class);
                view.getContext().startActivity(intent3);
                mainActivity.finish();
            }
        });
        builder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.show();
    }

    private void updateUserStatus(String status){
        String saveCurrentTime, saveCurrentDate;

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd. yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        HashMap<String, Object> StatusMap = new HashMap<>();
        StatusMap.put("time", saveCurrentTime);
        StatusMap.put("date", saveCurrentDate);
        StatusMap.put("state", status);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        String currentUserID = auth.getCurrentUser().getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.child("User").child(currentUserID).child("Status")
                .updateChildren(StatusMap);
    }

}