package com.example.wsins.wifiviewer.utils

import java.io.DataOutputStream
import java.io.File
import java.io.IOException

/**
 * Created by Sin on 2019/4/13
 */
class RootUtils {

    val isRoot: Boolean
        get() {
            var bool = false

            try {
                bool = !(!File("/system/bin/su").exists() && !File("/system/xbin/su").exists())
            } catch (e: Exception) {

            }
            return bool
        }

    private fun rumCmd(): Boolean {
        var process: Process? = null
        var os: DataOutputStream? = null
        val rt: Int
        try {
            process = getSUProcess()
            os = DataOutputStream(process?.outputStream)
            os.writeBytes("system/bin/mount -o rw,remount -t rootfs /data" + "\n")
            os.writeBytes("exit\n")
            os.flush()
            rt = process.waitFor()
        } catch (e: Exception) {
            return false
        } finally {
            if (os != null) {
                try {
                    os.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
            process?.destroy()
        }
        return rt == 0
    }

    fun checkRoot(): Boolean {
        return try {
            try {
                rumCmd()
            } catch (e: Exception) {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    fun getSUProcess(): Process = Runtime.getRuntime().exec("su")
}
