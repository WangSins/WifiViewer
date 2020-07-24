package com.example.wsins.wifiviewer.base

import java.lang.ref.Reference
import java.lang.ref.WeakReference


/**
 * Created by Sin on 2020/7/22
 */
abstract class BasePresenter<V : IBaseView> : IBasePresenter<V> {

    private var mViewReference: Reference<V>? = null

    override fun attachView(view: V) {
        mViewReference = WeakReference<V>(view)
    }

    /**
     * 获取view
     * @return 持有界面
     */
    fun getView(): V? {
        return if (isViewAttached()) {
            mViewReference?.get()
        } else null
    }

    /**
     * 判断view是否添加
     */
    private fun isViewAttached(): Boolean {
        return mViewReference != null && mViewReference!!.get() != null
    }

    override fun detachView() {
        if (mViewReference != null) {
            mViewReference!!.clear()
            mViewReference = null
        }
    }
}