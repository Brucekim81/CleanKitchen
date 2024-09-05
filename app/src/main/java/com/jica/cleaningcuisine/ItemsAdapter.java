package com.jica.cleaningcuisine;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class ItemsAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<AlarmInfo> items;
    private ItemsAlarmActivity itemAlarmActivity;

    public ItemsAdapter(Context context, ArrayList<AlarmInfo> items) {
        this.context = context;
        this.items = items;
        this.itemAlarmActivity = (ItemsAlarmActivity) context;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        }

        ImageView itemImage = convertView.findViewById(R.id.itemImage);
        TextView itemName = convertView.findViewById(R.id.itemName);
        Button btnAlarm = convertView.findViewById(R.id.btnAlarm);

        AlarmInfo item = items.get(position);
        String strImage = item.image;
        if( strImage.equals("dishwasher")){
            itemImage.setImageResource(R.drawable.dishwasher);
        }else if(strImage.equals("fridge")){
            itemImage.setImageResource(R.drawable.fridge);
        }else if(strImage.equals("microwave")){
            itemImage.setImageResource(R.drawable.microwave);
        }else if(strImage.equals("oven")){
            itemImage.setImageResource(R.drawable.oven);
        }

        itemName.setText(item.name);
        btnAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {itemAlarmActivity.showAlarmDialog(position);
            }
        });

        return convertView;
    }
}
