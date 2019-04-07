package com.example.wsins.wifiviewer.utils

import android.content.Context
import android.widget.Toast

object ToastUtils {

    private var toast: Toast? = null

    fun showToast(context: Context, msg: String) {
        toast = if (toast == null) {
            Toast.makeText(context, msg, Toast.LENGTH_SHORT)
        } else {
            toast?.cancel()
            Toast.makeText(context, msg, Toast.LENGTH_SHORT)
        }
        toast?.show()
    }
}