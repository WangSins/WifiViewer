package com.example.wsins.wifiviewer.base

import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.example.wsins.wifiviewer.listener.OnNextListener
import com.example.wsins.wifiviewer.util.ActivityManager
import com.example.wsins.wifiviewer.util.RootUtils

/**
 * Created by Sin on 2020/7/14
 */
abstract class BaseActivity : AppCompatActivity() {

    companion object {
        const val DATA_FIRST_LOAD = 0
        const val DATA_REFRESH = 1
        const val DATA_LOAD_MORE = 2
        val mHandler: Handler by lazy {
            Handler()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityManager.addActivity(this)
        setContentView(getLayoutResID())
        initRoot()
    }

    override fun onDestroy() {
        super.onDestroy()
        ActivityManager.removeActivity(this)
        this.release()
    }

    private fun initRoot() {
        RootUtils.checkRootAccess(this, object : OnNextListener {
            override fun onNext() {
                initActionBar()
                initView()
                initPresenter()
                initData()
                initEvent()
            }
        })
    }

    abstract fun getLayoutResID(): Int
    open fun initActionBar() {}
    open fun initView() {}
    open fun initPresenter() {}
    open fun initData() {}
    open fun initEvent() {}
    open fun release() {}

}