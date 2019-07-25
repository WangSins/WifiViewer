package com.example.wsins.wifiviewer.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.wsins.wifiviewer.info.WifiInfo

class WifiAdapter : RecyclerView.Adapter<WifiAdapter.WifiViewHolder>() {

    private var mWifiInfoList: MutableList<WifiInfo> = mutableListOf()

    fun setData(wifiInfoList: MutableList<WifiInfo>) {
        mWifiInfoList = wifiInfoList
        notifyDataSetChanged()
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
        val mItemView: View = LayoutInflater.from(p0.context).inflate(com.example.wsins.wifiviewer.R.layout.item_wifi, null)
        return WifiViewHolder(mItemView)
    }

    override fun getItemCount(): Int = mWifiInfoList.size

    override fun onBindViewHolder(p0: WifiViewHolder, p1: Int) {
        p0.tvWifiName.text = mWifiInfoList[p1].ssid
        p0.tvWifiPwd.text = mWifiInfoList[p1].password
        p0.itemView.setOnClickListener { onRecyclerViewItemClickListener.onRVItemClick(p1) }
        p0.itemView.setOnLongClickListener {
            onRecyclerViewItemClickListener.onRVItemLongClick(p1)
            true
        }
    }

    class WifiViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvWifiName: TextView = itemView.findViewById(com.example.wsins.wifiviewer.R.id.tv_wifi_name)
        val tvWifiPwd: TextView = itemView.findViewById(com.example.wsins.wifiviewer.R.id.tv_wifi_pwd)
    }
}
