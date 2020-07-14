package com.example.wsins.wifiviewer

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Rect
import android.os.Handler
import android.os.Message
import android.support.v4.view.GravityCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.OrientationHelper
import android.support.v7.widget.RecyclerView
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.example.wsins.wifiviewer.adapter.WifiAdapter
import com.example.wsins.wifiviewer.base.BaseActivity
import com.example.wsins.wifiviewer.bean.WifiBean
import com.example.wsins.wifiviewer.util.ShareUtils
import com.example.wsins.wifiviewer.util.WifiManager
import com.example.wsins.wifiviewer.util.dp
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.nav_header.view.*
import java.lang.ref.WeakReference
import kotlin.concurrent.thread

class MainActivity : BaseActivity() {

    private var mWifiLists: MutableList<WifiBean> = mutableListOf()
    private var mHandler = MyHandler(this)
    private lateinit var mWifiAdapter: WifiAdapter

    companion object {
        const val INIT_DATA = 0
        const val REFRESH_DATA = 1

        private class MyHandler(activity: MainActivity) : Handler() {
            private val mActivity: WeakReference<MainActivity> = WeakReference(activity)
            override fun handleMessage(msg: Message?) {
                super.handleMessage(msg)
                val activity = mActivity.get()
                when (msg?.what) {
                    INIT_DATA -> activity?.setData()
                    REFRESH_DATA -> {
                        activity?.run {
                            setData()
                            srl_wifi_list.also {
                                it.isRefreshing = false
                                snackBar(it, getString(R.string.refresh_complete))
                            }
                        }
                    }
                }
            }
        }
    }

    override fun getLayoutResID(): Int = R.layout.activity_main

    override fun initActionBar() {
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeButtonEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp)
        }
    }

    override fun initView() {
        val linearLayoutManager = LinearLayoutManager(this).apply {
            orientation = OrientationHelper.VERTICAL
        }
        mWifiAdapter = WifiAdapter()
        rv_wifi_list.run {
            layoutManager = linearLayoutManager
            adapter = mWifiAdapter
            addItemDecoration(object : RecyclerView.ItemDecoration() {

                override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                    super.getItemOffsets(outRect, view, parent, state)
                    outRect.run {
                        left = 8.dp
                        right = 8.dp
                        top = 8.dp
                    }
                }
            })
        }
    }

    override fun initData() {
        getData()
    }

    override fun initEvent() {
        mWifiAdapter.setOnItemClickListener(object : WifiAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                mWifiLists[position].password.run {
                    ShareUtils.copyTips(rv_wifi_list,
                            String.format(resources.getString(R.string.copied_pw_to_clipboard),
                                    this),
                            getString(R.string.share_pw),
                            this)
                }
            }

            override fun onItemLongClick(position: Int) {
                String.format(resources.getString(R.string.ssid_pw), mWifiLists[position].ssid, mWifiLists[position].password).run {
                    ShareUtils.copyTips(rv_wifi_list,
                            String.format(resources.getString(R.string.copied_ssid_pw_to_clipboard),
                                    mWifiLists[position].ssid),
                            getString(R.string.share_ssid_pw),
                            this)
                }

            }

        })
        srl_wifi_list.setOnRefreshListener {
            Handler().postDelayed({
                getData(REFRESH_DATA)
            }, 500)
        }
        nav_view.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_share -> {
                    ShareUtils.goShare(this@MainActivity,
                            getString(R.string.share_app), getString(R.string.share_app_information))
                }
                R.id.nav_about -> {
                    with(AlertDialog.Builder(this@MainActivity)) {
                        setTitle(context.getString(R.string.warm_prompt))
                        setMessage(context.getString(R.string.about_prompt_information))

                        setPositiveButton(context.getString(R.string.close)) { dialogInterface: DialogInterface, _: Int ->
                            dialogInterface.dismiss()
                        }
                        show()
                    }.run {
                        setCanceledOnTouchOutside(false)
                    }
                }
            }
            drawer_layout.closeDrawers()
            true
        }
    }

    override fun release() {
        mHandler.removeCallbacksAndMessages(null)
    }

    private fun getData(what: Int = INIT_DATA) {
        thread(true) {
            Message.obtain().also {
                it.what = what
                mWifiLists = WifiManager().readData()!!
                mHandler.sendMessage(it)
            }
        }
    }

    private fun setData() {
        nav_view.run {
            getHeaderView(0).app_name.text = getString(R.string.app_name)
            getHeaderView(0).wifi_count.text = String.format(resources.getString(R.string.a_total_of_n_wifi_messages), mWifiLists.size)
        }
        mWifiAdapter.setData(mWifiLists)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> drawer_layout.openDrawer(GravityCompat.START)
            R.id.item_setting -> {
                Intent().apply {
                    action = "android.net.wifi.PICK_WIFI_NETWORK"
                }.let {
                    startActivity(it)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private var mExitTime: Long = 0
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawers()
            return true
        } else {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                if ((System.currentTimeMillis() - mExitTime) > 2000) {
                    snackBar(rv_wifi_list, getString(R.string.press_exit_again))
                    mExitTime = System.currentTimeMillis()
                } else {
                    finish()
                }
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }
}
