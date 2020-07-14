package com.example.wsins.wifiviewer.util

import android.content.Context
import android.support.v7.app.AlertDialog
import com.example.wsins.wifiviewer.R
import com.example.wsins.wifiviewer.listener.OnNextListener
import java.io.DataOutputStream
import java.io.File
import java.io.IOException

/**
 * Created by Sin on 2019/4/13
 */
object RootUtils {

    val isRoot: Boolean
        get() {
            var bool = false
            try {
                bool = !(!File("/system/bin/su").exists() && !File("/system/xbin/su").exists())
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

    fun getSUProcess(): Process = Runtime.getRuntime().exec("su")

    fun checkRootAccess(context: Context, listener: OnNextListener) {
        if (isRoot) {
            if (!isGrant) {
                AlertDialog.Builder(context).run {
                    setTitle(context.getString(R.string.root_privilege_check))
                    setMessage(context.getString(R.string.unable_to_obtain_root_privileges))
                    setCancelable(false)
                    setPositiveButton(context.getString(R.string.sign_out)) { _, _ ->
                        ActivityManager.exitApp(context)
                    }
                    show()
                }
            } else {
                listener.onNext()
            }
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
