package com.example.wsins.wifiviewer.presenter

import com.example.wsins.wifiviewer.contract.WifiContract
import com.example.wsins.wifiviewer.base.BasePresenter
import com.example.wsins.wifiviewer.bean.WifiBean
import com.example.wsins.wifiviewer.model.WifiModel

/**
 * Created by Sin on 2020/7/21
 */
object WifiPresenter : BasePresenter<WifiContract.IWifiView>(), WifiContract.IWifiPresenter {

    private val mWifiModel: WifiModel by lazy {
        WifiModel
    }

    override fun readData(loadType: Int) {
        getView()?.onLoading(loadType)
        mWifiModel.readData(object : WifiModel.ReadCallback {
            override fun onSuccess(response: MutableList<WifiBean>) {
                if (response.size > 0) {
                    getView()?.loadSuccess(loadType, response)

                } else {
                    getView()?.onEmpty(loadType)
                }
            }

            override fun onError(errorCode: Int) {
                getView()?.loadError(loadType, errorCode)
            }
        })
    }
}