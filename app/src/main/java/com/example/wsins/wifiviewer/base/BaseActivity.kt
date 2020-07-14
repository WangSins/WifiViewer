package com.example.wsins.wifiviewer.base

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.example.wsins.wifiviewer.listener.OnNextListener
import com.example.wsins.wifiviewer.util.ActivityManager
import com.example.wsins.wifiviewer.util.RootUtils

/**
 * Created by Sin on 2020/7/14
 */
abstract class BaseActivity : AppCompatActivity() {

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
                initData()
                initEvent()
            }
        })
    }

    abstract fun getLayoutResID(): Int
    open fun initActionBar() {}
    open fun initView() {}
    open fun initData() {}
    open fun initEvent() {}
    open fun release() {}

    fun snackBar(view: View, text: String) {
        Snackbar.make(view, text, Snackbar.LENGTH_SHORT).show()
    }

}