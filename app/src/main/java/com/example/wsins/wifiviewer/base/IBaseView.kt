package com.example.wsins.wifiviewer.base


/**
 * Created by Sin on 2020/7/22
 */
interface IBaseView {
    /**
     * 加载失败回调
     */
    fun loadError(loadStyle: Int, errorCode: Int)

    /**
     * 加载中回调
     */
    fun onLoading(loadStyle: Int)

    /**
     * 空数据回调
     */
    fun onEmpty(loadStyle: Int)

}