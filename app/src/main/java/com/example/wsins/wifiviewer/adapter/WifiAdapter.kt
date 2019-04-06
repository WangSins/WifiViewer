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

    fun setData(wifiInfos: List<WifiInfo>) {
        mWifiInfos = wifiInfos
    }

    override fun getCount(): Int = mWifiInfos!!.size

    override fun getItem(position: Int): Any = mWifiInfos!![position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var mWifiViewHold: WifiViewHold
        var mItemView: View
        if (convertView == null) {
            mWifiViewHold = WifiViewHold()
            mItemView = LayoutInflater.from(mContext).inflate(R.layout.item_wifi, null)
            mWifiViewHold.tv_wifi_name = mItemView.findViewById(R.id.tv_wifi_name)
            mWifiViewHold.tv_wifi_pwd = mItemView.findViewById(R.id.tv_wifi_pwd)
            mItemView.tag = mWifiViewHold

        } else {
            mItemView = convertView
            mWifiViewHold = mItemView.tag as WifiViewHold
        }
        mWifiViewHold.tv_wifi_name.text = mWifiInfos!![position].ssid
        mWifiViewHold.tv_wifi_pwd.text = mWifiInfos!![position].password
        return mItemView
    }

    class WifiViewHold {
        lateinit var tv_wifi_name: TextView
        lateinit var tv_wifi_pwd: TextView
    }

}
