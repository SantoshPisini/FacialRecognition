package com.santosh.facialrecognition;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

class ListItem {
    private String name;
    private String roll;
    private int status;

    public ListItem(String roll, String name,int status) {
        this.name = name;
        this.roll = roll;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public String getRoll() {
        return roll;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.ViewHolder> implements PopupMenu.OnMenuItemClickListener {

    private List<ListItem> listItems;
    private Context context;

    public StudentAdapter(List<ListItem> listItems, Context context) {
        this.context = context;
        this.listItems = listItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item, viewGroup, false));
    }
    String who ="";
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        final ListItem item;
        item = listItems.get(position);
        viewHolder.name1.setText(item.getName());
        viewHolder.roll.setText(item.getRoll());
        viewHolder.img_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                who = item.getRoll();
                showPopup(view);
            }
        });

    }

    public void showPopup(View v){
        PopupMenu popupMenu = new PopupMenu(v.getContext(),v);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.inflate(R.menu.student_list_popup);
        popupMenu.show();

    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.student_list_popup_change:
                ListActivity la = new ListActivity();
                List<ListItem> li = new ArrayList<>();
                li.clear();
                for(ListItem item: la.getListItems()){
                    li.add(item);
                }
                for(ListItem item: li) {
                    if(item.getRoll().equals(who)) {
                        if (item.getStatus() == 0)
                            item.setStatus(1);
                        else
                            item.setStatus(0);
                    }
                }
                la.setUpLists(li,context);
                break;
                case R.id.student_list_popup_profile:
                    Intent i = new Intent(context.getApplicationContext(),StudentProfileActivity.class);
                    i.putExtra("id",who);
                    context.startActivity(i);break;
        }
        return true;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView name1;
        private TextView roll;
        private ImageButton img_btn;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name1 = itemView.findViewById(R.id.tv_name1);
            roll = itemView.findViewById(R.id.tv_roll);
            img_btn = itemView.findViewById(R.id.imageButton);
        }

    }
}
