package com.example.wsins.wifiviewer.adapter

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import com.google.android.material.snackbar.Snackbar
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.wsins.wifiviewer.R
import com.example.wsins.wifiviewer.bean.WifiBean

class WifiAdapter : RecyclerView.Adapter<WifiAdapter.WifiViewHolder>() {

    private var mWifiLists: MutableList<WifiBean> = mutableListOf()
    private lateinit var mContext: Context

    fun setData(wifiLists: MutableList<WifiBean>) {
        with(DiffUtil.calculateDiff(WifiDiffCallBack(mWifiLists, wifiLists))) {
            dispatchUpdatesTo(this@WifiAdapter)
        }
        mWifiLists.clear()
        mWifiLists.addAll(wifiLists)
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): WifiViewHolder {
        mContext = p0.context
        return WifiViewHolder(LayoutInflater.from(p0.context).inflate(R.layout.item_wifi, p0, false))
    }

    override fun getItemCount(): Int = mWifiLists.size

    override fun onBindViewHolder(p0: WifiViewHolder, p1: Int) {
        with(mWifiLists[p1]) {
            p0.tvWifiName.text = ssid
            p0.tvWifiPwd.text = password
            p0.itemView.setOnClickListener {
                with(this.password) {
                    copyAndShare(p0.itemView,
                            String.format(mContext.getString(R.string.copied_pw_to_clipboard),
                                    this),
                            mContext.getString(R.string.share_pw),
                            this)
                }
            }
            p0.itemView.setOnLongClickListener {
                with(String.format(mContext.getString(R.string.ssid_pw), ssid, password)) {
                    copyAndShare(p0.itemView,
                            String.format(mContext.getString(R.string.copied_ssid_pw_to_clipboard),
                                    ssid),
                            mContext.getString(R.string.share_ssid_pw),
                            this)
                }
                true
            }
        }
    }

    private fun copyAndShare(view: View, msg: String, shareTitle: String, shareContent: String) {
        (mContext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager).run {
            setPrimaryClip(ClipData.newPlainText("", shareContent))
        }
        Snackbar.make(view, msg, Snackbar.LENGTH_SHORT)
                .setAction(mContext.getString(R.string.share)) {
                    with(Intent(Intent.ACTION_SEND)) {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, shareContent)
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        mContext.startActivity(Intent.createChooser(this, shareTitle))
                    }
                }
                .show()
    }

    class WifiViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvWifiName: TextView = itemView.findViewById(R.id.tv_wifi_name)
        val tvWifiPwd: TextView = itemView.findViewById(R.id.tv_wifi_pwd)
    }
}
