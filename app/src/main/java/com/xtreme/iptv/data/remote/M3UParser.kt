
package com.xtreme.iptv.data.remote
import com.xtreme.iptv.data.model.Channel

object M3UParser {
    fun parse(content: String): List<Channel> {
        val lines = content.lines()
        val result = mutableListOf<Channel>()
        var currentName = ""
        var currentLogo = ""
        var currentGroup = "General"
        var currentEpg = ""
        for (line in lines) {
            val l = line.trim()
            if (l.startsWith("#EXTINF:")) {
                currentName = l.substringAfter(",").trim().ifEmpty { "Unknown" }
                currentLogo = Regex("tvg-logo="(.*?)"").find(l)?.groupValues?.get(1) ?: ""
                currentGroup = Regex("group-title="(.*?)"").find(l)?.groupValues?.get(1) ?: "General"
                currentEpg = Regex("tvg-id="(.*?)"").find(l)?.groupValues?.get(1) ?: ""
            } else if (l.isNotEmpty() && !l.startsWith("#")) {
                if (l.startsWith("http")) {
                    result.add(Channel(
                        id = (currentEpg.ifEmpty { currentName }),
                        name = currentName,
                        logo = currentLogo,
                        group = currentGroup,
                        url = l,
                        epgId = currentEpg
                    ))
                    currentName = ""; currentLogo = ""; currentGroup = "General"
                }
            }
        }
        return result
    }
}
