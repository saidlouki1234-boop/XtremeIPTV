
package com.xtreme.iptv.ui.viewmodel
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.xtreme.iptv.XtremeApp
import com.xtreme.iptv.data.local.SavedPlaylist
import com.xtreme.iptv.data.model.Channel
import com.xtreme.iptv.data.remote.M3UParser
import com.xtreme.iptv.data.remote.XtreamApi
import com.xtreme.iptv.data.remote.XtreamHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainViewModel(app: Application) : AndroidViewModel(app) {
    private val db = (app as XtremeApp).db
    private val _channels = MutableStateFlow<List<Channel>>(emptyList())
    val channels: StateFlow<List<Channel>> = _channels
    private val _categories = MutableStateFlow<List<String>>(emptyList())
    val categories: StateFlow<List<String>> = _categories
    private val _playlists = MutableStateFlow<List<SavedPlaylist>>(emptyList())
    val playlists: StateFlow<List<SavedPlaylist>> = _playlists

    init { loadPlaylists() }

    fun loadPlaylists() = viewModelScope.launch {
        _playlists.value = db.playlistDao().getAll()
    }

    fun addM3U(name: String, url: String, contentDirect: String? = null) = viewModelScope.launch {
        val content = contentDirect ?: try {
            OkHttpClient().newCall(okhttp3.Request.Builder().url(url).build()).execute().body?.string() ?: ""
        } catch (e: Exception) { "" }
        val parsed = M3UParser.parse(content)
        _channels.value = parsed
        _categories.value = parsed.map { it.group }.distinct()
        db.playlistDao().insert(SavedPlaylist(name=name, type="M3U", url=url))
        loadPlaylists()
    }

    fun addXtream(name: String, host: String, user: String, pass: String) = viewModelScope.launch {
        val retrofit = Retrofit.Builder().baseUrl(host.trimEnd('/')+"/").addConverterFactory(GsonConverterFactory.create()).build()
        val api = retrofit.create(XtreamApi::class.java)
        try {
            val streams = api.getLiveStreams(user, pass)
            val cats = api.getLiveCategories(user, pass)
            val catMap = cats.associateBy { it.category_id }
            val list = streams.map {
                Channel(
                    id = it.stream_id.toString(),
                    name = it.name,
                    logo = it.stream_icon,
                    group = catMap[it.category_id]?.category_name ?: "Live",
                    url = XtreamHelper.buildStreamUrl(host, user, pass, it.stream_id),
                    epgId = it.epg_channel_id ?: ""
                )
            }
            _channels.value = list
            _categories.value = list.map { it.group }.distinct()
            db.playlistDao().insert(SavedPlaylist(name=name, type="XTREAM", url=host, host=host, username=user, password=pass))
            loadPlaylists()
        } catch (e: Exception) { e.printStackTrace() }
    }

    fun filterByCategory(cat: String): List<Channel> {
        return if (cat == "All") _channels.value else _channels.value.filter { it.group == cat }
    }
}
