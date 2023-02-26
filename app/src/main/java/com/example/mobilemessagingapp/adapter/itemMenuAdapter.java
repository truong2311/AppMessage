package com.example.mobilemessagingapp.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobilemessagingapp.R;
import com.example.mobilemessagingapp.models.itemMenu;

import java.util.List;

public class itemMenuAdapter extends RecyclerView.Adapter<itemMenuAdapter.itemMenuViewHolder> {

    private List<itemMenu> itemMenuList;
    private Context context;

    private IOnClick iOnClick;

    public interface IOnClick{
        void OnClick(itemMenu itemMenu);
    }

    public itemMenuAdapter(List<itemMenu> menuList, IOnClick iOnClick){
        this.itemMenuList = menuList;
        this.iOnClick = iOnClick;
    }

    @NonNull
    @Override
    public itemMenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_menu, parent, false);
        return new itemMenuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull itemMenuViewHolder holder, int position) {
        itemMenu itemMenu = itemMenuList.get(position);
        if (itemMenu == null) {
            return;
        }

        holder.icon.setImageResource(itemMenu.getImage());
        holder.name.setText(itemMenu.getName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iOnClick.OnClick(itemMenu);
            }
        });
//        holder.layoutitem.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                switch (itemMenu.getIditem()) {
//                    case 1:
//                        Intent intent1 = new Intent(view.getContext(), ProfileActivity.class);
//                        view.getContext().startActivity(intent1);
//                        break;
//                    case 2:
//                        Intent intent2 = new Intent(view.getContext(), ChangePasswordActivity.class);
//                        view.getContext().startActivity(intent2);
//                        break;
//                    case 3:
//                        signOutAcc(view);
//                        break;
//                }
//            }
//        });
    }

    public void release() {
        context = null;
    }

    @Override
    public int getItemCount() {
        if (itemMenuList != null) {
            return itemMenuList.size();
        }
        return 0;
    }

    public class itemMenuViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout layoutitem;
        private int id;
        private ImageView icon;
        private TextView name;

        public itemMenuViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutitem = itemView.findViewById(R.id.itemMenu);
            icon = itemView.findViewById(R.id.icon_item_menu);
            name = itemView.findViewById(R.id.tv_item_menu);
        }
    }


}
