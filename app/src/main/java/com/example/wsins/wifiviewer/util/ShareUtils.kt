package com.example.wsins.wifiviewer.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.support.design.widget.Snackbar
import android.view.View
import com.example.wsins.wifiviewer.R

/**
 * Created by Sin on 2020/7/14
 */
object ShareUtils {

    fun copyClipBoard(context: Context, content: String) {
        (context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager).run {
            primaryClip = ClipData.newPlainText("", content)
        }
    }

    fun goShare(context: Context, title: String, content: String) {
        with(Intent(Intent.ACTION_SEND)) {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, content)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(Intent.createChooser(this, title))
        }
    }

    fun copyTips(view: View, msg: String, shareTitle: String, shareContent: String) {
        copyClipBoard(view.context, shareContent)
        Snackbar.make(view, msg, Snackbar.LENGTH_SHORT)
                .setAction(view.context.getString(R.string.share)) { goShare(view.context, shareTitle, shareContent) }
                .show()
    }
}