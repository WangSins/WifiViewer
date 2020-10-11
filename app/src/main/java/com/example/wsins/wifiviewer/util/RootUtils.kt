package com.example.wsins.wifiviewer.util

import android.content.Context
import androidx.appcompat.app.AlertDialog
import com.example.wsins.wifiviewer.R
import com.example.wsins.wifiviewer.listener.OnNextListener
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.File
import java.io.InputStreamReader

/**
 * Created by Sin on 2019/4/13
 */
object RootUtils {

    val isRoot: Boolean
        get() {
            var bool = false
            try {
                bool = checkSuPath() || checkSuFile()
            } catch (e: Exception) {
            }
            return bool
        }

    val isGrant: Boolean
        get() {
            var bool = false
            try {
                bool = checkGrant()
            } catch (e: Exception) {
            }
            return bool
        }

    private fun checkSuPath(): Boolean {
        var process: Process? = null
        var `in`: BufferedReader? = null
        return try {
            process = Runtime.getRuntime().exec(arrayOf("which", "su"))
            `in` = BufferedReader(InputStreamReader(process.inputStream))
            `in`.readLine() != null
        } catch (t: Throwable) {
            false
        } finally {
            `in`?.close()
            process?.destroy()
        }
    }

    private fun checkSuFile(): Boolean {
        var bool = false
        val paths = arrayOf(
                "/sbin/su", "/system/bin/su", "/system/xbin/su",
                "/data/local/su", "/data/local/bin/su", "/data/local/xbin/su",
                "/system/sd/xbin/su", "/system/bin/failsafe/su"
        )
        for (path in paths) {
            if (File(path).exists()) {
                bool = true
                break
            }
        }
        return bool
    }

    private fun checkSuperuserApk(): Boolean {
        var bool = false
        val paths = arrayOf("/system/app/Superuser.apk", "/system/priv-app/Superuser.apk")
        for (path in paths) {
            if (File(path).exists()) {
                bool = true
                break
            }
        }
        return bool
    }

    private fun checkGrant(): Boolean {
        var process: Process? = null
        var os: DataOutputStream? = null
        val rt: Int
        try {
            process = getSUProcess()
            os = DataOutputStream(process.outputStream)
            os.writeBytes("system/bin/mount -o rw,remount -t rootfs /data" + "\n")
            os.writeBytes("exit\n")
            os.flush()
            rt = process.waitFor()
        } catch (e: Exception) {
            return false
        } finally {
            os?.close()
            process?.destroy()
        }
        return rt == 0
    }

    fun getSUProcess(): Process = Runtime.getRuntime().exec("su")

    fun checkRootAccess(context: Context, listener: OnNextListener) {
        if (isRoot) {
            listener.onNext()
        } else {
            AlertDialog.Builder(context).run {
                setTitle(context.getString(R.string.root_privilege_check))
                setMessage(context.getString(R.string.this_equipment_is_not_authorized))
                setCancelable(false)
                setPositiveButton(context.getString(R.string.sign_out)) { _, _ ->
                    ActivityManager.exitApp(context)
                }
                show()
            }
        }
    }
}
