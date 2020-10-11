package com.eshen.wifiviewer.contract

import com.eshen.wifiviewer.base.IBaseView
import com.eshen.wifiviewer.bean.WifiBean

/**
 * Created by Sin on 2020/7/24
 */
class WifiContract {

    interface IWifiPresenter {
        /**
         * 加载数据
         */
        fun readData(loadType: Int)
    }

    interface IWifiView : IBaseView {
        /**
         * 加载成功回调
         */
        fun loadSuccess(loadStyle: Int, response: MutableList<WifiBean>)
    }
}