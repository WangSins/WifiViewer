package com.example.wsins.wifiviewer.util

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import java.util.*
import kotlin.system.exitProcess

/**
 * Created by Sin on 2020/7/13
 */

object ActivityManager {

    private val activityStack: Stack<Activity> = Stack()

    /**
     * 添加Activity
     */
    fun addActivity(activity: Activity) {
        activityStack.add(activity)
    }

    /**
     * 移出存在的Activity
     */
    fun removeActivity(activity: Activity) {
        if (activityStack.contains(activity)) {
            activity.finish()
            activityStack.remove(activity)
        }
    }

    /**
     * 获取最上面的Activity
     */
    fun getTopActivity(): Activity {
        return activityStack.lastElement()
    }

    /**
     * 清除Activity栈
     */
    private fun clearActivity() {
        for (activity in activityStack) {
            activity.finish()
        }
        activityStack.clear()
    }

    /**
     *  退出应用程序
     */
    fun exitApp(context: Context) {
        //先清除Activity
        clearActivity()
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        //再killProcesses
        activityManager.killBackgroundProcesses(context.packageName)
        exitProcess(0)
    }
}