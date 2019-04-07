package com.example.wsins.wifiviewer.utils

import com.example.wsins.wifiviewer.info.WifiInfo
import org.w3c.dom.Element
import org.xml.sax.SAXException
import java.io.*
import java.util.*
import java.util.regex.Pattern
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.ParserConfigurationException

class WifiManage {

    lateinit var wifiInfos: MutableList<WifiInfo>
    lateinit var wifiInfo: WifiInfo

    lateinit var process: Process

    private var dataOutputStream: DataOutputStream? = null
    private var dataInputStream: DataInputStream? = null
    lateinit var byteArrayInputStream: ByteArrayInputStream

    private var line: String? = null

    fun readData(): List<WifiInfo>? {
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
            process = getSUProcess()
            dataOutputStream = DataOutputStream(process.outputStream).apply {
                writeBytes("cat /data/misc/wifi/$fileName\n")
                writeBytes("exit\n")
                flush()
            }
            dataInputStream = DataInputStream(process.inputStream)
            val inputStreamReader = InputStreamReader(dataInputStream, "UTF-8")
            val bufferedReader = BufferedReader(inputStreamReader)
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
                process.destroy()
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

    private fun parseConf(wifiData: StringBuffer): List<WifiInfo>? {
        wifiInfos = ArrayList()
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
                    wifiInfos.add(wifiInfo)
                }
            }
        }
        return wifiInfos
    }

    private fun parseXml(wifiData: StringBuffer): List<WifiInfo>? {
        wifiInfos = ArrayList()
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
                val network_list = (items.item(0) as Element).getElementsByTagName("Network")
                for (i in 0 until network_list.length) {
                    val item = (network_list.item(i) as Element).getElementsByTagName("WifiConfiguration")
                    if (item.length < 1) {
                        continue
                    }
                    val elem = item.item(0) as Element
                    val wp_node_list = elem.getElementsByTagName("string")
                    if (wp_node_list.length < 2) {
                        continue
                    }
                    wifiInfo = WifiInfo()
                    for (j in 0 until wp_node_list.length) {
                        val e = wp_node_list.item(j) as Element
                        val name = e.getAttribute("name")
                        val value = e.firstChild.nodeValue

                        if ("SSID" == name) {
                            wifiInfo.ssid = value.replace("\"", "")
                        } else if ("PreSharedKey" == name) {
                            wifiInfo.password = value.replace("\"", "")
                            wifiInfos.add(wifiInfo)
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
        return wifiInfos
    }

    private fun getSUProcess(): Process {
        return Runtime.getRuntime().exec("su")
    }

}
