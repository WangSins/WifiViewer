package com.example.wsins.wifiviewer.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.example.wsins.wifiviewer.R

import com.example.wsins.wifiviewer.info.WifiInfo
import com.example.wsins.wifiviewer.viewholder.WifiViewHolder

class WifiAdapter(val mContext: Context) : BaseAdapter() {

    lateinit var mWifiInfos: List<WifiInfo>

    fun setData(wifiInfos: List<WifiInfo>) {
        mWifiInfos = wifiInfos
    }

    override fun getCount(): Int = mWifiInfos.size

    override fun getItem(position: Int): Any = mWifiInfos[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val mWifiViewHolder: WifiViewHolder
        val mItemView: View
        if (convertView == null) {
            mWifiViewHolder = WifiViewHolder()
            mItemView = LayoutInflater.from(mContext).inflate(R.layout.item_wifi, null)
            mWifiViewHolder.tv_wifi_name = mItemView.findViewById(R.id.tv_wifi_name)
            mWifiViewHolder.tv_wifi_pwd = mItemView.findViewById(R.id.tv_wifi_pwd)
            mItemView.tag = mWifiViewHolder

        } else {
            mItemView = convertView
            mWifiViewHolder = mItemView.tag as WifiViewHolder
        }
        mWifiViewHolder.tv_wifi_name.text = mWifiInfos[position].ssid
        mWifiViewHolder.tv_wifi_pwd.text = mWifiInfos[position].password
        return mItemView
    }
}
