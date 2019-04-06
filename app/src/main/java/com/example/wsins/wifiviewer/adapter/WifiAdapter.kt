package com.example.wsins.wifiviewer.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.wsins.wifiviewer.R

import com.example.wsins.wifiviewer.info.WifiInfo

class WifiAdapter(val mContext: Context) : BaseAdapter() {

    private var mWifiInfos: List<WifiInfo>? = null
    private var tv_wifi_name: TextView? = null
    private var tv_wifi_pwd: TextView? = null

    fun setData(wifiInfos: List<WifiInfo>) {
        mWifiInfos = wifiInfos
    }

    override fun getCount(): Int = mWifiInfos!!.size

    override fun getItem(position: Int): Any = mWifiInfos!![position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = LayoutInflater.from(mContext).inflate(R.layout.item_wifi, null)
        tv_wifi_name = convertView.findViewById(R.id.tv_wifi_name)
        tv_wifi_pwd = convertView.findViewById(R.id.tv_wifi_pwd)
        tv_wifi_name!!.text = mWifiInfos!![position].ssid
        tv_wifi_pwd!!.text = mWifiInfos!![position].password
        return convertView
    }

}
