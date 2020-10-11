package com.example.wsins.wifiviewer

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Rect
import android.net.Uri
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import androidx.core.view.GravityCompat
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.example.wsins.wifiviewer.adapter.WifiAdapter
import com.example.wsins.wifiviewer.base.BaseActivity
import com.example.wsins.wifiviewer.bean.WifiBean
import com.example.wsins.wifiviewer.contract.WifiContract
import com.example.wsins.wifiviewer.presenter.WifiPresenter
import com.example.wsins.wifiviewer.util.ActivityManager
import com.example.wsins.wifiviewer.util.dp
import com.example.wsins.wifiviewer.util.showSnackBar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.empty_layout.*
import kotlinx.android.synthetic.main.nav_header.view.*

class MainActivity() : BaseActivity(), WifiContract.IWifiView,
        SwipeRefreshLayout.OnRefreshListener, NavigationView.OnNavigationItemSelectedListener {

    private lateinit var mWifiAdapter: WifiAdapter
    private lateinit var mPresenter: WifiPresenter
    private var mExitTime: Long = 0

    override fun getLayoutResID(): Int = R.layout.activity_main

    override fun initActionBar() {
        setSupportActionBar(toolbar)
        supportActionBar?.run {
            setDisplayHomeAsUpEnabled(true)
            setHomeButtonEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp)
        }
    }

    override fun initView() {
        mWifiAdapter = WifiAdapter()
        wifi_list_rv.run {
            layoutManager = LinearLayoutManager(this@MainActivity)
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

    override fun initPresenter() {
        mPresenter = WifiPresenter
        mPresenter.attachView(this)
    }

    override fun initData() {
        mPresenter.readData(DATA_FIRST_LOAD)
    }

    override fun initEvent() {
        wifi_list_srl.setOnRefreshListener(this)
        nav_view.setNavigationItemSelectedListener(this)
    }

    override fun release() {
        mPresenter.detachView()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> drawer_layout.openDrawer(GravityCompat.START)
            R.id.item_setting -> {
                startActivity(Intent().apply {
                    action = "android.net.wifi.PICK_WIFI_NETWORK"
                })
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawers()
            return true
        } else {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                if ((System.currentTimeMillis() - mExitTime) > 2000) {
                    wifi_list_rv.showSnackBar(getString(R.string.press_exit_again), Snackbar.LENGTH_SHORT)
                    mExitTime = System.currentTimeMillis()
                } else {
                    finish()
                }
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun loadSuccess(loadStyle: Int, response: MutableList<WifiBean>) {
        nav_view.run {
            getHeaderView(0).app_name.text = getString(R.string.app_name)
            getHeaderView(0).wifi_count.text = String.format(resources.getString(R.string.a_total_of_n_wifi_messages), response.size)
        }
        mWifiAdapter.setData(response)
        wifi_list_srl.isRefreshing = false
        empty_layout.visibility = View.GONE
    }

    override fun loadError(loadStyle: Int, errorCode: Int) {
        wifi_list_srl.isRefreshing = false
        with(AlertDialog.Builder(this@MainActivity)) {
            setTitle(context.getString(R.string.root_privilege_check))
            setMessage(context.getString(R.string.unable_to_obtain_root_privileges))
            setCancelable(false)
            setPositiveButton(context.getString(R.string.sign_out)) { _, _ ->
                ActivityManager.exitApp(context)
            }
            show()
        }
    }

    override fun onLoading(loadStyle: Int) {
        wifi_list_srl.isRefreshing = true
    }

    override fun onEmpty(loadStyle: Int) {
        wifi_list_srl.isRefreshing = false
        empty_layout.visibility = View.VISIBLE
    }

    override fun onRefresh() {
        mPresenter.readData(DATA_REFRESH)
    }

    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
        when (p0.itemId) {
            R.id.nav_share -> {
                startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, getString(R.string.share_app_information))
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }, getString(R.string.share_app)))
            }
            R.id.nav_about -> {
                with(AlertDialog.Builder(this@MainActivity)) {
                    setTitle(context.getString(R.string.warm_prompt))
                    setMessage(context.getString(R.string.about_prompt_information))
                    setNegativeButton(context.getString(R.string.open_source)) { _: DialogInterface?, _: Int ->
                        startActivity(Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://github.com/WangSins/WifiViewer")
                        ))
                    }
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
        return true
    }
}
