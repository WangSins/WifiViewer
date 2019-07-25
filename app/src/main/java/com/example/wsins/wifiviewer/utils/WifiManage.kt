package com.example.wsins.wifiviewer.utils

import com.example.wsins.wifiviewer.info.WifiInfo
import org.w3c.dom.Element
import org.xml.sax.SAXException
import java.io.*
import java.util.regex.Pattern
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.ParserConfigurationException

class WifiManage {

    lateinit var wifiInfo: WifiInfo
    var wifiInfoList: MutableList<WifiInfo> = mutableListOf()

    fun readData(): MutableList<WifiInfo>? {
        var process: Process? = null
        var dataOutputStream: DataOutputStream? = null
        var dataInputStream: DataInputStream? = null
        val isO: Boolean
        val fileName: String
        if (android.os.Build.VERSION.SDK_INT >= 26) {
            fileName = "*.xml"
            isO = true
        } else {
            fileName = "*.conf"
            isO = false
        }
        val wifiData = StringBuffer()
        try {
            process = RootUtils().getSUProcess()
            dataOutputStream = DataOutputStream(process.outputStream).apply {
                writeBytes("cat /data/misc/wifi/$fileName\n")
                writeBytes("exit\n")
                flush()
            }
            dataInputStream = DataInputStream(process.inputStream)
            val inputStreamReader = InputStreamReader(dataInputStream, "UTF-8")
            val bufferedReader = BufferedReader(inputStreamReader)
            var line: String? = null
            while ({ line = bufferedReader.readLine();line }() != null) {
                wifiData.append(line)
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
                process?.destroy()
            } catch (e: Exception) {
                throw e
            }
        }
        return if (isO) {
            parseXml(wifiData)
        } else {
            parseConf(wifiData)
        }
    }

    private fun parseConf(wifiData: StringBuffer): MutableList<WifiInfo>? {
        val network = Pattern.compile("network=\\{([^\\}]+)\\}", Pattern.DOTALL)
        val networkMatcher = network.matcher(wifiData.toString())
        while (networkMatcher.find()) {
            val networkBlock = networkMatcher.group()
            val ssid = Pattern.compile("ssid=\"([^\"]+)\"")
            val ssidMatcher = ssid.matcher(networkBlock)
            if (ssidMatcher.find()) {
                wifiInfo = WifiInfo()
                wifiInfo.ssid = ssidMatcher.group(1)
                val psk = Pattern.compile("psk=\"([^\"]+)\"")
                val pskMatcher = psk.matcher(networkBlock)
                if (pskMatcher.find()) {
                    wifiInfo.password = pskMatcher.group(1)
                    wifiInfoList.add(wifiInfo)
                }
            }
        }
        return wifiInfoList
    }

    private fun parseXml(wifiData: StringBuffer): MutableList<WifiInfo>? {
        var byteArrayInputStream: ByteArrayInputStream? = null
        val factory = DocumentBuilderFactory.newInstance()
        try {
            byteArrayInputStream = ByteArrayInputStream(wifiData.toString().toByteArray(charset("UTF-8")))
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }

        try {
            val builder = factory.newDocumentBuilder()
            val document = builder.parse(byteArrayInputStream)
            val root = document.documentElement
            val items = root.getElementsByTagName("NetworkList")
            if (items.length > 0) {
                val networkList = (items.item(0) as Element).getElementsByTagName("Network")
                for (i in 0 until networkList.length) {
                    val item = (networkList.item(i) as Element).getElementsByTagName("WifiConfiguration")
                    if (item.length < 1) {
                        continue
                    }
                    val elem = item.item(0) as Element
                    val wpNodeList = elem.getElementsByTagName("string")
                    if (wpNodeList.length < 2) {
                        continue
                    }
                    wifiInfo = WifiInfo()
                    for (j in 0 until wpNodeList.length) {
                        val e = wpNodeList.item(j) as Element
                        val name = e.getAttribute("name")
                        val value = e.firstChild.nodeValue

                        if ("SSID" == name) {
                            wifiInfo.ssid = value.replace("\"", "")
                        } else if ("PreSharedKey" == name) {
                            wifiInfo.password = value.replace("\"", "")
                            wifiInfoList.add(wifiInfo)
                        }
                    }
                }
            }
        } catch (e: ParserConfigurationException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: SAXException) {
            e.printStackTrace()
        }
        return wifiInfoList
    }

}
