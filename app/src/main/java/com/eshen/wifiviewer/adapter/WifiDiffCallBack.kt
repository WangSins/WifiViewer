package com.eshen.wifiviewer.adapter

import androidx.recyclerview.widget.DiffUtil
import com.eshen.wifiviewer.bean.WifiBean

/**
 * Created by Sin on 2020/7/29
 */
class WifiDiffCallBack(private val oldDates: List<WifiBean>, private val newDates: List<WifiBean>) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldDates.size

    override fun getNewListSize(): Int = newDates.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldBean = oldDates[oldItemPosition]
        val newBean = newDates[newItemPosition]
        return oldBean == newBean
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val (oldSSID, oldPassword) = oldDates[oldItemPosition]
        val (newSSID, newPassword) = newDates[newItemPosition]
        return oldSSID == newSSID && oldPassword == newPassword
    }

}