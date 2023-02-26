package com.example.mobilemessagingapp.activities;

import androidx.annotation.NonNull;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;

import com.example.mobilemessagingapp.BroadcastReceiver;
import com.example.mobilemessagingapp.R;
import com.example.mobilemessagingapp.fragment.ChatListFragment;
import com.example.mobilemessagingapp.fragment.ChatGroupListFragment;
import com.example.mobilemessagingapp.fragment.MenuFragment;
import com.example.mobilemessagingapp.fragment.RequestFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private DatabaseReference databaseReference;
    private String currentUserID;

    private BroadcastReceiver broadcastReceiver;

    private final String[] labels = new String[]{"Chat List", "Chat Group", "Request", "Menu"};
    private final int[] icons = new int[]{R.drawable.icon_tab_chatlist, R.drawable.icon_tab_chatgroup, R.drawable.icon_tab_request, R.drawable.icon_tab_menu};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mapping();

        auth = FirebaseAuth.getInstance();
        currentUserID = auth.getCurrentUser().getUid();

        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("User").child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!(snapshot.child("UserName").exists())) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                    alertDialogBuilder.setTitle(R.string.request);
                    alertDialogBuilder.setMessage(R.string.log_in_first_time);
                    alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                            startActivity(intent);
                        }
                    });
                    alertDialogBuilder.show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }

    private void mapping() {

        broadcastReceiver = new BroadcastReceiver();

        TabLayout tabLayout = findViewById(R.id.tab_layout);
        ViewPager2 viewPager2 = findViewById(R.id.view_pager2);
        ViewPagerFragmentAdapter adapter = new ViewPagerFragmentAdapter(this);
        viewPager2.setAdapter(adapter);

        // bind and set tabLayout to viewPager2 and set labels for every tab
        new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> tab.setIcon(icons[position])).attach();

        // set default position to 1 instead of default 0
        viewPager2.setCurrentItem(0, false);

    }

    private class ViewPagerFragmentAdapter extends FragmentStateAdapter {

        public ViewPagerFragmentAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }
        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0:
                    return new ChatListFragment();
                case 1:
                    return new ChatGroupListFragment();
                case 2:
                    return new RequestFragment();
                case 3:
                    return new MenuFragment();
            }
            return new ChatListFragment();
        }

        @Override
        public int getItemCount() {
            return labels.length;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        //control status user
        FirebaseUser currentUser = auth.getCurrentUser();
        if(currentUser != null){
            updateUserStatus("online");
        }
        //register broadcastReceiver
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onStop() {
        super.onStop();
        //control status user
        FirebaseUser currentUser = auth.getCurrentUser();
        if(currentUser != null){
            updateUserStatus("offline");
        }

        //unregister broadcastReceiver
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FirebaseUser currentUser = auth.getCurrentUser();
        if(currentUser != null){
            updateUserStatus("offline");
        }
    }

    private void updateUserStatus(String status){
        String saveCurrentTime, saveCurrentDate;

        Calendar calendar = Calendar.getInstance();

        @SuppressLint("SimpleDateFormat") SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd. yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        @SuppressLint("SimpleDateFormat") SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        HashMap<String, Object> StatusMap = new HashMap<>();
        StatusMap.put("time", saveCurrentTime);
        StatusMap.put("date", saveCurrentDate);
        StatusMap.put("state", status);

        databaseReference.child("User").child(currentUserID).child("Status")
                .updateChildren(StatusMap);

    }

//    public void gotoBlankFragment(){
//        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
//        fragmentTransaction.replace(R.id.lnMainActivity, new BlankFragment());
//        fragmentTransaction.commit();
//    }
}