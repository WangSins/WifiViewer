package com.example.wsins.wifiviewer.utils

import android.content.Context
import android.content.pm.PackageManager

class VersionCodeUtils {

    fun getVersionCode(mContext: Context): Int {
        var versionCode = 0
        try {
            versionCode = mContext.packageManager.getPackageInfo(mContext.packageName, 0).versionCode
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        return versionCode
    }

    fun getVerName(context: Context): String {
        var verName = ""
        try {
            verName = context.packageManager.getPackageInfo(context.packageName, 0).versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        return verName
    }
}
