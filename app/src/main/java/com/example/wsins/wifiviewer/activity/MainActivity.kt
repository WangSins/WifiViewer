package com.example.wsins.wifiviewer.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import com.example.wsins.wifiviewer.R
import com.example.wsins.wifiviewer.adapter.WifiAdapter
import com.example.wsins.wifiviewer.info.WifiInfo
import com.example.wsins.wifiviewer.utils.ClipBoardUtils
import com.example.wsins.wifiviewer.utils.ToastUtils
import com.example.wsins.wifiviewer.utils.WifiManage
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), AdapterView.OnItemLongClickListener, AdapterView.OnItemClickListener {

    lateinit var wifiAdapter: WifiAdapter
    lateinit var mWifiInfos: List<WifiInfo>

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
        when (item?.itemId) {
            R.id.item_flash -> {
                initData()
                wifiAdapter.setData(mWifiInfos)
                wifiAdapter.notifyDataSetChanged()
                ToastUtils.showToast(this, "刷新完成。")
            }
            R.id.item_setting -> {
                val intent = Intent()
                intent.action = "android.net.wifi.PICK_WIFI_NETWORK"
                startActivity(intent)
            }
            R.id.item_about -> {
                AboutActivity.move(this)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    @Throws(Exception::class)
    fun initData() {
        WifiManage().also {
            mWifiInfos = it.readData()!!
        }
    }

    private fun initView() {
        wifiAdapter = WifiAdapter(this@MainActivity)
        wifiAdapter.setData(mWifiInfos)
        lv_wifi_list.adapter = wifiAdapter
    }

    private fun initListener() {
        lv_wifi_list.onItemLongClickListener = this
        lv_wifi_list.onItemClickListener = this
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
