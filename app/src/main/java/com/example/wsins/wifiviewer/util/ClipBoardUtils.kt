package com.example.wsins.wifiviewer.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context

object ClipBoardUtils {

    fun copyClipBoard(context: Context, label: String, text: String) {
        (context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager).run {
            primaryClip = ClipData.newPlainText(label, text)
        }
    }
}