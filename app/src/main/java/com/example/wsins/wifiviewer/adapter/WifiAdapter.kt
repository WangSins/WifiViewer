package com.example.wsins.wifiviewer.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.wsins.wifiviewer.R
import com.example.wsins.wifiviewer.bean.WifiBean

class WifiAdapter : RecyclerView.Adapter<WifiAdapter.WifiViewHolder>() {

    private var mWifiLists: MutableList<WifiBean> = mutableListOf()

    fun setData(wifiLists: MutableList<WifiBean>) {
        mWifiLists = wifiLists
        notifyDataSetChanged()
    }

    private lateinit var onItemClickListener: OnItemClickListener

    interface OnItemClickListener {
        fun onItemClick(position: Int)
        fun onItemLongClick(position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.onItemClickListener = listener
    }


    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): WifiViewHolder {
        val mItemView: View = LayoutInflater.from(p0.context).inflate(R.layout.item_wifi, p0, false)
        return WifiViewHolder(mItemView)
    }

    override fun getItemCount(): Int = mWifiLists.size

    override fun onBindViewHolder(p0: WifiViewHolder, p1: Int) {
        p0.tvWifiName.text = mWifiLists[p1].ssid
        p0.tvWifiPwd.text = mWifiLists[p1].password
        p0.itemView.setOnClickListener { onItemClickListener.onItemClick(p1) }
        p0.itemView.setOnLongClickListener {
            onItemClickListener.onItemLongClick(p1)
            true
        }
    }

    class WifiViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvWifiName: TextView = itemView.findViewById(R.id.tv_wifi_name)
        val tvWifiPwd: TextView = itemView.findViewById(R.id.tv_wifi_pwd)
    }
}
