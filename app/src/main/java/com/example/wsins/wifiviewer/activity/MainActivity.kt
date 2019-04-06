package com.example.wsins.wifiviewer.activity

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import android.widget.Toast

import com.example.wsins.wifiviewer.R
import com.example.wsins.wifiviewer.utils.WifiManage
import com.example.wsins.wifiviewer.adapter.WifiAdapter
import com.example.wsins.wifiviewer.info.WifiInfo

class MainActivity : AppCompatActivity(), AdapterView.OnItemLongClickListener {

    private var wifiAdapter: WifiAdapter? = null
    private var lv_wifi_list: ListView? = null
    private var mWifiInfos: List<WifiInfo>? = null

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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.item_flash -> {
                initData()
                wifiAdapter!!.setData(mWifiInfos!!)
                wifiAdapter!!.notifyDataSetChanged()
            }
            R.id.item_setting -> {
                val intent = Intent()
                intent.action = "android.net.wifi.PICK_WIFI_NETWORK"
                startActivity(intent)
            }
            R.id.item_about -> {
                val intent = Intent()
                intent.setClass(this, AboutActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    @Throws(Exception::class)
    fun initData() {
        WifiManage().also {
            mWifiInfos = it.Read()
        }
    }

    private fun initView() {
        lv_wifi_list = findViewById(com.example.wsins.wifiviewer.R.id.lv_wifi_list)
        wifiAdapter = WifiAdapter(this@MainActivity)
        wifiAdapter!!.setData(mWifiInfos!!)
        lv_wifi_list!!.adapter = wifiAdapter
    }

    private fun initListener() {
        lv_wifi_list!!.onItemLongClickListener = this
    }

    override fun onItemLongClick(parent: AdapterView<*>, view: View, position: Int, id: Long): Boolean {
        mClipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        mclipData = ClipData.newPlainText("wifipwd", mWifiInfos!![position].password).also {
            mClipboardManager!!.primaryClip = it
        }
        Toast.makeText(this@MainActivity, "密码复制成功。", Toast.LENGTH_LONG).show()
        return false
    }

    private var mExitTime: Long = 0
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                Toast.makeText(this, "再按一次退出程序。", Toast.LENGTH_SHORT).show()
                mExitTime = System.currentTimeMillis()
            } else {
                finish()
            }
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

}
