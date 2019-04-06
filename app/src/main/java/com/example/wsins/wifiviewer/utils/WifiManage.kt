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

    lateinit var wifiInfos: MutableList<WifiInfo>
    lateinit var wifiInfo: WifiInfo

    lateinit var process: Process

    private var dataOutputStream: DataOutputStream? = null
    private var dataInputStream: DataInputStream? = null
    lateinit var inputStreamReader: InputStreamReader
    lateinit var bufferedReader: BufferedReader
    lateinit var wifiConf: StringBuffer

    lateinit var network: Pattern
    lateinit var ssid: Pattern
    lateinit var psk: Pattern

    lateinit var networkMatcher: Matcher
    lateinit var ssidMatcher: Matcher
    lateinit var pskMatcher: Matcher

    lateinit var networkBlock: String
    private var line: String? = null

    @Throws(Exception::class)
    fun Read(): List<WifiInfo>? {

        wifiInfos = ArrayList()
        wifiConf = StringBuffer()
        try {
            process = Runtime.getRuntime().exec("su")
            dataOutputStream = DataOutputStream(process.outputStream).apply {
                writeBytes("cat /data/misc/wifi/*.conf\n")
                writeBytes("exit\n")
                flush()
            }
            dataInputStream = DataInputStream(process.inputStream)
            inputStreamReader = InputStreamReader(dataInputStream, "UTF-8")
            bufferedReader = BufferedReader(inputStreamReader)
            while ({ line = bufferedReader.readLine();line }() != null) {
                wifiConf.append(line)
            }
            bufferedReader.close()
            inputStreamReader.close()
            process.waitFor()
        } catch (e: Exception) {
            throw e
        } finally {
            try {
                dataOutputStream?.close()
                dataInputStream?.close()
                process.destroy()
            } catch (e: Exception) {
                throw e
            }
        }
        return parseData(wifiConf)
    }

    private fun parseData(wifiConf: StringBuffer): List<WifiInfo>? {
        network = Pattern.compile("network=\\{([^\\}]+)\\}", Pattern.DOTALL)
        networkMatcher = network.matcher(wifiConf.toString())
        while (networkMatcher.find()) {
            networkBlock = networkMatcher.group()
            ssid = Pattern.compile("ssid=\"([^\"]+)\"")
            ssidMatcher = ssid.matcher(networkBlock)
            if (ssidMatcher.find()) {
                wifiInfo = WifiInfo()
                wifiInfo.ssid = ssidMatcher.group(1)
                psk = Pattern.compile("psk=\"([^\"]+)\"")
                pskMatcher = psk.matcher(networkBlock)
                if (pskMatcher.find()) {
                    wifiInfo.password = pskMatcher.group(1)
                } else {
                    wifiInfo.password = "无密码"
                }
                wifiInfos.add(wifiInfo)
            }
        }
        return wifiInfos
    }

}
