package com.eshen.wifiviewer.base

interface IBasePresenter<V : IBaseView> {
    /**
     * 添加关联
     */
    fun attachView(view: V)

    /**
     * 取消关联
     */
    fun detachView()
}