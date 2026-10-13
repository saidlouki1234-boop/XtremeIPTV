
package com.xtreme.iptv.data.remote
import com.xtreme.iptv.data.model.XtreamCategory
import com.xtreme.iptv.data.model.XtreamStream
import retrofit2.http.GET
import retrofit2.http.Query

interface XtreamApi {
    @GET("player_api.php")
    suspend fun getLiveCategories(@Query("username") u: String, @Query("password") p: String, @Query("action") a: String = "get_live_categories"): List<XtreamCategory>

    @GET("player_api.php")
    suspend fun getLiveStreams(@Query("username") u: String, @Query("password") p: String, @Query("action") a: String = "get_live_streams", @Query("category_id") catId: String? = null): List<XtreamStream>

    @GET("player_api.php")
    suspend fun getVodCategories(@Query("username") u: String, @Query("password") p: String, @Query("action") a: String = "get_vod_categories"): List<XtreamCategory>
}

object XtreamHelper {
    fun buildStreamUrl(host: String, user: String, pass: String, streamId: Int): String {
        val cleanHost = host.trimEnd('/')
        return "$cleanHost/live/$user/$pass/$streamId.m3u8"
    }
    fun buildEpgUrl(host: String, user: String, pass: String): String {
        return "${host.trimEnd('/')}/xmltv.php?username=$user&password=$pass"
    }
}
