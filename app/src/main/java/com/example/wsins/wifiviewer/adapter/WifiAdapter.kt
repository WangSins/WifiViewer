package com.example.wsins.wifiviewer.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

import com.example.wsins.wifiviewer.info.WifiInfo

class WifiAdapter(val mContext: Context) : BaseAdapter() {

    private var mWifiInfos: List<WifiInfo>? = null
    private var tv_wifi_brief: TextView? = null

    fun setData(wifiInfos: List<WifiInfo>) {
        mWifiInfos = wifiInfos
    }

    override fun getCount(): Int = mWifiInfos!!.size

    override fun getItem(position: Int): Any = mWifiInfos!![position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = LayoutInflater.from(mContext).inflate(android.R.layout.simple_list_item_1, null)
        tv_wifi_brief = convertView.findViewById(android.R.id.text1)
        tv_wifi_brief!!.text = "Wifi:" + mWifiInfos!![position].ssid + "\n密码:" + mWifiInfos!![position].password
        return convertView
    }

}
