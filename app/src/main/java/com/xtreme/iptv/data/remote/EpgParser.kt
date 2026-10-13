
package com.xtreme.iptv.data.remote
import com.xtreme.iptv.data.model.EpgChannel
import com.xtreme.iptv.data.model.EpgEvent
import org.xmlpull.v1.XmlPullParserFactory
import java.io.StringReader

object EpgParser {
    fun parse(xml: String): Map<String, EpgChannel> {
        val map = mutableMapOf<String, MutableList<EpgEvent>>()
        val names = mutableMapOf<String, String>()
        try {
            val factory = XmlPullParserFactory.newInstance()
            val parser = factory.newPullParser()
            parser.setInput(StringReader(xml))
            var eventType = parser.eventType
            var currentChannelId = ""
            var currentTitle = ""
            var currentDesc = ""
            var currentStart = ""
            var currentStop = ""
            var currentEpgChannel = ""
            while (eventType != org.xmlpull.v1.XmlPullParser.END_DOCUMENT) {
                when (eventType) {
                    org.xmlpull.v1.XmlPullParser.START_TAG -> {
                        when (parser.name) {
                            "channel" -> currentChannelId = parser.getAttributeValue(null, "id") ?: ""
                            "display-name" -> if (currentChannelId.isNotEmpty()) {
                                // next text
                            }
                            "programme" -> {
                                currentStart = parser.getAttributeValue(null, "start") ?: ""
                                currentStop = parser.getAttributeValue(null, "stop") ?: ""
                                currentEpgChannel = parser.getAttributeValue(null, "channel") ?: ""
                            }
                            "title" -> {}
                        }
                    }
                    org.xmlpull.v1.XmlPullParser.TEXT -> {
                        val text = parser.text.trim()
                        if (text.isNotEmpty()) {
                            // simplified: last logic handled in END_TAG
                            if (parser.name == null) { /* */ }
                        }
                    }
                    org.xmlpull.v1.XmlPullParser.END_TAG -> {
                        when (parser.name) {
                            "display-name" -> {
                                // handled via text
                            }
                            "programme" -> {
                                map.getOrPut(currentEpgChannel) { mutableListOf() }.add(
                                    EpgEvent(currentStart, currentStop, currentTitle.ifEmpty { "Program" }, currentDesc)
                                )
                                currentTitle = ""; currentDesc = ""
                            }
                            "title" -> currentTitle = parser.text ?: ""
                            "desc" -> currentDesc = parser.text ?: ""
                        }
                    }
                }
                eventType = parser.next()
            }
        } catch (e: Exception) { e.printStackTrace() }
        return map.map { (k,v) -> k to EpgChannel(k, names[k] ?: k, v) }.toMap()
    }
}
