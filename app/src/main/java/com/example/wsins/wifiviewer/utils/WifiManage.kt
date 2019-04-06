package com.example.wsins.wifiviewer.utils

import com.example.wsins.wifiviewer.info.WifiInfo

import java.io.BufferedReader
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.util.ArrayList
import java.util.regex.Matcher
import java.util.regex.Pattern

class WifiManage {

    private var wifiInfos: MutableList<WifiInfo>? = null
    private var wifiInfo: WifiInfo? = null

    private var process: Process? = null

    private var dataOutputStream: DataOutputStream? = null
    private var dataInputStream: DataInputStream? = null
    private var inputStreamReader: InputStreamReader? = null
    private var bufferedReader: BufferedReader? = null

    private var network: Pattern? = null
    private var ssid: Pattern? = null
    private var psk: Pattern? = null

    private var networkMatcher: Matcher? = null
    private var ssidMatcher: Matcher? = null
    private var pskMatcher: Matcher? = null

    private var line: String? = null
    private var networkBlock: String? = null
    private var wifiConf: StringBuffer? = null

    @Throws(Exception::class)
    fun Read(): List<WifiInfo>? {

        wifiInfos = ArrayList()
        wifiConf = StringBuffer()
        try {
            process = Runtime.getRuntime().exec("su")
            dataOutputStream = DataOutputStream(process!!.outputStream).apply {
                writeBytes("cat /data/misc/wifi/*.conf\n")
                writeBytes("exit\n")
                flush()
            }
            dataInputStream = DataInputStream(process!!.inputStream)
            inputStreamReader = InputStreamReader(dataInputStream, "UTF-8")
            bufferedReader = BufferedReader(inputStreamReader)
            while ({ line = bufferedReader!!.readLine();line }() != null) {
                wifiConf!!.append(line)
            }
            bufferedReader!!.close()
            inputStreamReader!!.close()
            process!!.waitFor()
        } catch (e: Exception) {
            throw e
        } finally {
            try {
                if (dataOutputStream != null) {
                    dataOutputStream!!.close()
                }
                if (dataInputStream != null) {
                    dataInputStream!!.close()
                }
                process!!.destroy()
            } catch (e: Exception) {
                throw e
            }

        }

        return parseData(wifiConf!!)
    }

    private fun parseData(wifiConf: StringBuffer): List<WifiInfo>? {
        network = Pattern.compile("network=\\{([^\\}]+)\\}", Pattern.DOTALL)
        networkMatcher = network!!.matcher(wifiConf.toString())
        while (networkMatcher!!.find()) {
            networkBlock = networkMatcher!!.group()
            ssid = Pattern.compile("ssid=\"([^\"]+)\"")
            ssidMatcher = ssid!!.matcher(networkBlock)
            if (ssidMatcher!!.find()) {
                wifiInfo = WifiInfo()
                wifiInfo!!.ssid = ssidMatcher!!.group(1)
                psk = Pattern.compile("psk=\"([^\"]+)\"")
                pskMatcher = psk!!.matcher(networkBlock)
                if (pskMatcher!!.find()) {
                    wifiInfo!!.password = pskMatcher!!.group(1)
                } else {
                    wifiInfo!!.password = "无密码"
                }
                wifiInfos!!.add(wifiInfo!!)
            }
        }
        return wifiInfos
    }

}
