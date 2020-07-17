package com.example.wsins.wifiviewer.util

import android.os.Handler
import android.os.Looper
import com.example.wsins.wifiviewer.bean.WifiBean
import org.w3c.dom.Element
import org.xml.sax.SAXException
import java.io.*
import java.util.regex.Pattern
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.ParserConfigurationException
import kotlin.concurrent.thread

object WifiManager {

    private val isO: Boolean
        get() {
            return android.os.Build.VERSION.SDK_INT >= 26
        }

    private val mHandler: Handler by lazy {
        Handler(Looper.getMainLooper())
    }

    interface ReadCallback<T> {
        fun onSuccess(response: MutableList<T>)
        fun onError(errorCode: Int)
    }

    fun readData(callback: ReadCallback<WifiBean>) {
        thread {
            var process: Process? = null
            var dataOutputStream: DataOutputStream? = null
            var dataInputStream: DataInputStream? = null
            val fileName: String = if (isO) {
                "*.xml"
            } else {
                "*.conf"
            }
            val wifiData = StringBuffer()
            try {
                process = RootUtils.getSUProcess()
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
                val rt = process.waitFor()
                if (rt == 0) {
                    val wifiLists = if (isO) {
                        parseXml(wifiData.toString())
                    } else {
                        parseConf(wifiData.toString())
                    }
                    mHandler.post {
                        callback.onSuccess(wifiLists)
                    }
                } else {
                    mHandler.post {
                        callback.onError(rt)
                    }
                }
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
        }
    }

    private fun parseConf(wifiData: String): MutableList<WifiBean> {
        val wifiLists: MutableList<WifiBean> = mutableListOf()
        val network = Pattern.compile("network=\\{([^\\}]+)\\}", Pattern.DOTALL)
        val networkMatcher = network.matcher(wifiData.toString())
        while (networkMatcher.find()) {
            val networkBlock = networkMatcher.group()
            val ssid = Pattern.compile("ssid=\"([^\"]+)\"")
            val ssidMatcher = ssid.matcher(networkBlock)
            if (ssidMatcher.find()) {
                val wifiBean = WifiBean()
                wifiBean.ssid = ssidMatcher.group(1)
                val psk = Pattern.compile("psk=\"([^\"]+)\"")
                val pskMatcher = psk.matcher(networkBlock)
                if (pskMatcher.find()) {
                    wifiBean.password = pskMatcher.group(1)
                    wifiLists.add(wifiBean)
                }
            }
        }
        return wifiLists
    }

    private fun parseXml(wifiData: String): MutableList<WifiBean> {
        val wifiLists: MutableList<WifiBean> = mutableListOf()
        var byteArrayInputStream: ByteArrayInputStream? = null
        val factory = DocumentBuilderFactory.newInstance()
        try {
            byteArrayInputStream = ByteArrayInputStream(wifiData.toByteArray(charset("UTF-8")))
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
                    val wifiBean = WifiBean()
                    for (j in 0 until wpNodeList.length) {
                        val e = wpNodeList.item(j) as Element
                        val name = e.getAttribute("name")
                        val value = e.firstChild.nodeValue

                        if ("SSID" == name) {
                            wifiBean.ssid = value.replace("\"", "")
                        } else if ("PreSharedKey" == name) {
                            wifiBean.password = value.replace("\"", "")
                            wifiLists.add(wifiBean)
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
        return wifiLists
    }

}
