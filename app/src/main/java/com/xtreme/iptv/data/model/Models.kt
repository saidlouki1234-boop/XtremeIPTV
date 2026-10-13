
package com.xtreme.iptv.data.model

data class Channel(
    val id: String,
    val name: String,
    val logo: String = "",
    val group: String = "General",
    val url: String,
    val epgId: String = ""
)

data class Category(val name: String, val count: Int)

data class XtreamCredentials(val host: String, val username: String, val password: String)

// Xtream API responses
data class XtreamCategory(val category_id: String, val category_name: String)
data class XtreamStream(val stream_id: Int, val name: String, val stream_icon: String, val category_id: String, val epg_channel_id: String?)
data class EpgEvent(val start: String, val stop: String, val title: String, val desc: String = "")
data class EpgChannel(val id: String, val displayName: String, val events: List<EpgEvent> = emptyList())

// M3U parsed
data class Playlist(val channels: List<Channel>, val groups: List<String>)
