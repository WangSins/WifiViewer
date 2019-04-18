package com.example.wsins.wifiviewer.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.wsins.wifiviewer.info.WifiInfo

class WifiAdapter(val mContext: Context) : RecyclerView.Adapter<WifiAdapter.WifiViewHolder>() {

    private var mWifiInfos: MutableList<WifiInfo> = mutableListOf()

    fun setData(wifiInfos: MutableList<WifiInfo>) {
        mWifiInfos = wifiInfos
    }

    private lateinit var onRecyclerViewItemClickListener: OnRVItemClickListener

    interface OnRVItemClickListener {
        fun onRVItemClick(position: Int)
        fun onRVItemLongClick(position: Int)
    }

    fun setOnRVItemClickListener(onItemClickListener: OnRVItemClickListener) {
        onRecyclerViewItemClickListener = onItemClickListener
    }


    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): WifiViewHolder {
        val mItemView: View = LayoutInflater.from(mContext).inflate(com.example.wsins.wifiviewer.R.layout.item_wifi, null)
        return WifiViewHolder(mItemView)
    }

    override fun getItemCount(): Int = mWifiInfos.size

    override fun onBindViewHolder(p0: WifiViewHolder, p1: Int) {
        p0.tv_wifi_name.text = mWifiInfos[p1].ssid
        p0.tv_wifi_pwd.text = mWifiInfos[p1].password
        p0.itemView.setOnClickListener { onRecyclerViewItemClickListener.onRVItemClick(p1) }
        p0.itemView.setOnLongClickListener {
            onRecyclerViewItemClickListener.onRVItemLongClick(p1)
            true
        }
    }

    class WifiViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tv_wifi_name: TextView = itemView.findViewById(com.example.wsins.wifiviewer.R.id.tv_wifi_name)
        val tv_wifi_pwd: TextView = itemView.findViewById(com.example.wsins.wifiviewer.R.id.tv_wifi_pwd)
    }
}
