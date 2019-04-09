package com.example.wsins.wifiviewer.activity

import android.content.Intent
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import com.example.wsins.wifiviewer.adapter.WifiAdapter
import com.example.wsins.wifiviewer.info.WifiInfo
import com.example.wsins.wifiviewer.utils.ClipBoardUtils
import com.example.wsins.wifiviewer.utils.ToastUtils
import com.example.wsins.wifiviewer.utils.WifiManage
import kotlinx.android.synthetic.main.activity_main.*
import android.os.Handler

class MainActivity : AppCompatActivity(), AdapterView.OnItemLongClickListener, AdapterView.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {

    lateinit var mWifiAdapter: WifiAdapter
    lateinit var mWifiInfos: List<WifiInfo>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.wsins.wifiviewer.R.layout.activity_main)
        getData()
        initView()
        initListener()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(com.example.wsins.wifiviewer.R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            com.example.wsins.wifiviewer.R.id.item_setting -> {
                val intent = Intent().apply {
                    action = "android.net.wifi.PICK_WIFI_NETWORK"
                }
                startActivity(intent)
            }
            com.example.wsins.wifiviewer.R.id.item_about -> {
                AboutActivity.move(this)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun getData() {
        mWifiInfos = WifiManage().readData()!!
    }

    private fun initView() {
        mWifiAdapter = WifiAdapter(this@MainActivity)
        mWifiAdapter.setData(mWifiInfos)
        lv_wifi_list.adapter = mWifiAdapter
    }

    private fun initListener() {
        lv_wifi_list.run {
            onItemLongClickListener = this@MainActivity
            onItemClickListener = this@MainActivity
        }
        srl_wifi_list.setOnRefreshListener(this)
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        ClipBoardUtils().copyClipBoard(this, "wifipwd", mWifiInfos[position].password)
        ToastUtils.showToast(this, "已复制密码 ${mWifiInfos[position].password} 到剪贴板。")
    }

    override fun onItemLongClick(parent: AdapterView<*>, view: View, position: Int, id: Long): Boolean {
        ClipBoardUtils().copyClipBoard(this, "wifissidpwd", "SSID：" + mWifiInfos[position].ssid + "\n密码：" + mWifiInfos[position].password)
        ToastUtils.showToast(this, "已复制 ${mWifiInfos[position].ssid} 的SSID和密码到剪贴板。")
        return true
    }

    override fun onRefresh() {
        Handler().postDelayed({
            getData()
            mWifiAdapter.setData(mWifiInfos)
            mWifiAdapter.notifyDataSetChanged()
            srl_wifi_list.isRefreshing = false
        }, 500)
    }

    private var mExitTime: Long = 0
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                ToastUtils.showToast(this, "再按一次退出程序。")
                mExitTime = System.currentTimeMillis()
            } else {
                finish()
            }
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

}
