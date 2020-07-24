package com.example.wsins.wifiviewer.contract

import com.example.wsins.wifiviewer.base.IBaseView
import com.example.wsins.wifiviewer.bean.WifiBean

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