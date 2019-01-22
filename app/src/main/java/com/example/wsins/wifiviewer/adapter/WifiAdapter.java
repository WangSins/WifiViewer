package com.example.wsins.wifiviewer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.wsins.wifiviewer.info.WifiInfo;

import java.util.List;

public class WifiAdapter extends BaseAdapter {

    List<WifiInfo> wifiInfos = null;
    Context con;

    public WifiAdapter(List<WifiInfo> wifiInfos, Context con) {
        this.wifiInfos = wifiInfos;
        this.con = con;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return wifiInfos.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return wifiInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        convertView = LayoutInflater.from(con).inflate(android.R.layout.simple_list_item_1, null);
        TextView tv = (TextView) convertView.findViewById(android.R.id.text1);
        tv.setText("Wifi:" + wifiInfos.get(position).ssid + "\n密码:" + wifiInfos.get(position).password);
        return convertView;
    }

}
