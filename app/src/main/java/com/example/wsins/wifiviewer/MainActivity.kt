package com.example.wsins.wifiviewer

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.design.widget.Snackbar
import android.support.v4.view.GravityCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.OrientationHelper
import android.support.v7.widget.RecyclerView
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import com.example.wsins.wifiviewer.adapter.WifiRVAdapter
import com.example.wsins.wifiviewer.data.WifiManager
import com.example.wsins.wifiviewer.info.WifiInfo
import com.example.wsins.wifiviewer.util.ClipBoardUtils
import com.example.wsins.wifiviewer.util.DensityUtils
import com.example.wsins.wifiviewer.util.RootUtils
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.ref.WeakReference
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    private var mWifiInfoList: MutableList<WifiInfo> = mutableListOf()
    private var mHandler = MyHandler(this)
    private lateinit var mWifiRVAdapter: WifiRVAdapter

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
                                Snackbar.make(it, getString(R.string.refresh_complete), Snackbar.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
        }
    }

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initRoot()
    }

    private fun initRoot() {
        if (RootUtils.isRoot) {
            if (!RootUtils.checkRoot()) {
                AlertDialog.Builder(this).run {
                    setTitle(getString(R.string.root_privilege_check))
                    setMessage(getString(R.string.unable_to_obtain_root_privileges))
                    setCancelable(false)
                    setPositiveButton(getString(R.string.sign_out)) { _, _ ->
                        this@MainActivity.finish()
                    }
                    show()
                }
            } else {
                initActionBar()
                initView()
                getData()
                initListener()
            }
        } else {
            AlertDialog.Builder(this).run {
                setTitle(getString(R.string.root_privilege_check))
                setMessage(getString(R.string.this_equipment_is_not_authorized))
                setCancelable(false)
                setPositiveButton(getString(R.string.sign_out)) { _, _ ->
                    this@MainActivity.finish()
                }
                show()
            }
        }
    }

    private fun getData(what: Int = INIT_DATA) {
        thread(true) {
            val msg = Message.obtain().also {
                it.what = what
            }
            mWifiInfoList = WifiManager().readData()!!
            mHandler.sendMessage(msg)
        }
    }

    private fun setData() {
        nav_view.apply {
            getHeaderView(0).findViewById<TextView>(R.id.app_name).text = getString(R.string.app_name)
            getHeaderView(0).findViewById<TextView>(R.id.wifi_count).text = String.format(resources.getString(R.string.a_total_of_n_wifi_messages), mWifiInfoList.size)
        }
        mWifiRVAdapter.setData(mWifiInfoList)
    }

    private fun initActionBar() {
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeButtonEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp)
        }
    }

    private fun initView() {
        val layoutManager = LinearLayoutManager(this).apply {
            orientation = OrientationHelper.VERTICAL
        }
        rv_wifi_list.layoutManager = layoutManager
        mWifiRVAdapter = WifiRVAdapter()
        rv_wifi_list.addItemDecoration(object : RecyclerView.ItemDecoration() {

            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                super.getItemOffsets(outRect, view, parent, state)
                outRect.run {
                    left = DensityUtils.dip2px(this@MainActivity, 8f)
                    right = DensityUtils.dip2px(this@MainActivity, 8f)
                    top = DensityUtils.dip2px(this@MainActivity, 8f)
                }
            }
        })
        rv_wifi_list.adapter = mWifiRVAdapter
    }

    private fun initListener() {
        mWifiRVAdapter.setOnRVItemClickListener(object : WifiRVAdapter.OnRVItemClickListener {
            override fun onRVItemClick(position: Int) {
                val textWifiPW = mWifiInfoList[position].password
                ClipBoardUtils.copyClipBoard(this@MainActivity, "textWifiPW", textWifiPW)
                Snackbar.make(rv_wifi_list, String.format(resources.getString(R.string.copied_pw_to_clipboard), textWifiPW), Snackbar.LENGTH_SHORT)
                        .setAction(getString(R.string.share)) {
                            textShare(getString(R.string.share_pw), textWifiPW)
                        }
                        .show()
            }

            override fun onRVItemLongClick(position: Int) {
                val textWifiSSIDAndPW = String.format(resources.getString(R.string.ssid_pw), mWifiInfoList[position].ssid,mWifiInfoList[position].password)
                ClipBoardUtils.copyClipBoard(this@MainActivity, "textWifiSSIDAndPW", textWifiSSIDAndPW)
                Snackbar.make(rv_wifi_list, String.format(resources.getString(R.string.copied_ssid_pw_to_clipboard), mWifiInfoList[position].ssid), Snackbar.LENGTH_SHORT)
                        .setAction(getString(R.string.share)) {
                            textShare(getString(R.string.share_ssid_pw), textWifiSSIDAndPW)
                        }
                        .show()
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
                    textShare(getString(R.string.share_app), getString(R.string.share_app_information))
                }
                R.id.nav_about -> {
                    AlertDialog.Builder(this).run {
                        setTitle(getString(R.string.warm_prompt))
                        setMessage(getString(R.string.about_prompt_information))
                        setCancelable(false)
                        setPositiveButton(getString(R.string.close)) { dialogInterface: DialogInterface, i: Int ->
                            dialogInterface.dismiss()
                        }
                        show()
                    }
                }
            }
            drawer_layout.closeDrawers()
            true
        }
    }

    private fun textShare(title: String, text: String) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, text)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(Intent.createChooser(intent, title))
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> drawer_layout.openDrawer(GravityCompat.START)
            R.id.item_setting -> {
                val intent = Intent().apply {
                    action = "android.net.wifi.PICK_WIFI_NETWORK"
                }
                startActivity(intent)
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
                    Snackbar.make(rv_wifi_list, getString(R.string.press_exit_again), Snackbar.LENGTH_SHORT).show()
                    mExitTime = System.currentTimeMillis()
                } else {
                    finish()
                }
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onDestroy() {
        super.onDestroy()
        mHandler.removeCallbacksAndMessages(null)
    }

}