package com.example.wsins.wifiviewer.activity

import android.os.Bundle
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.example.wsins.wifiviewer.R
import com.example.wsins.wifiviewer.utils.VersionCodeUtils
import kotlinx.android.synthetic.main.activity_about.*

class AboutActivity : AppCompatActivity() {
    private var actionBar: ActionBar? = null
    private var versionName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        initActionBar()
        initData()
        initView()
    }

    private fun initActionBar() {
        actionBar = supportActionBar
        if (actionBar != null) {
            actionBar!!.apply {
                title = "关于"
                setHomeButtonEnabled(true)
                setDisplayHomeAsUpEnabled(true)
            }
        }
    }

    private fun initData() {
        versionName = VersionCodeUtils().getVerName(this)
    }

    private fun initView() {
        tv_brief.text = "这是作者闲暇时间为自己备用机开发的一个App。\n目前支持的功能有：\n1.对获取本地保存的WiFi信息。\n2.长按对密码进行复制。"
        tv_version.text = "当前版本：v${versionName}"
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
