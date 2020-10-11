package com.eshen.wifiviewer.presenter

import com.eshen.wifiviewer.contract.WifiContract
import com.eshen.wifiviewer.base.BasePresenter
import com.eshen.wifiviewer.bean.WifiBean
import com.eshen.wifiviewer.model.WifiModel

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