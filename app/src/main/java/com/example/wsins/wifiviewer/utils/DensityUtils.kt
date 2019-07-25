package com.example.wsins.wifiviewer.utils

import android.content.Context

object DensityUtils {

    /**
     * 根据手机屏幕从dip的单位准换成px(像素)
     *
     * @param context
     * @param dpValue
     * @return
     */
    fun dip2px(context: Context, dpValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()

    }

    /**
     * 根据手机屏幕从px()像素的单位转换为dip
     *
     * @param context
     * @param pxValue
     * @return
     */
    fun px2dip(context: Context, pxValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (pxValue / scale + 0.5f).toInt()

    }
}
