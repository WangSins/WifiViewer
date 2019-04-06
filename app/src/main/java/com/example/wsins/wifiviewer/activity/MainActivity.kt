package com.example.wsins.wifiviewer.activity

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import android.widget.Toast

import com.example.wsins.wifiviewer.R
import com.example.wsins.wifiviewer.utils.WifiManage
import com.example.wsins.wifiviewer.adapter.WifiAdapter
import com.example.wsins.wifiviewer.info.WifiInfo

class MainActivity : AppCompatActivity(), AdapterView.OnItemLongClickListener {

    private var mWifiInfos: List<WifiInfo>? = null
    private var lv_wifi_list: ListView? = null

    private var mclipData: ClipData? = null
    private var mClipboardManager: ClipboardManager? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        try {
            initData()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        initView()
        initListener()
    }

    @Throws(Exception::class)
    fun initData() {
        WifiManage().also {
            mWifiInfos = it.Read()
        }
    }

    private fun initView() {
        lv_wifi_list = findViewById(R.id.lv_wifi_list)
        lv_wifi_list!!.adapter = WifiAdapter(this@MainActivity).apply {
            setData(mWifiInfos!!)
        }
    }

    private fun initListener() {
        lv_wifi_list!!.onItemLongClickListener = this
    }

    override fun onItemLongClick(parent: AdapterView<*>, view: View, position: Int, id: Long): Boolean {
        mClipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        mclipData = ClipData.newPlainText("wifipwd", mWifiInfos!![position].password).also {
            mClipboardManager!!.primaryClip = it
        }
        Toast.makeText(this@MainActivity, "密码复制成功！", Toast.LENGTH_LONG).show()
        return false
    }
}
