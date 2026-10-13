
package com.xtreme.iptv.data.local
import androidx.room.*

@Entity
data class SavedPlaylist(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val type: String, // M3U or XTREAM
    val url: String,
    val host: String = "",
    val username: String = "",
    val password: String = "",
    val lastSync: Long = System.currentTimeMillis()
)

@Entity
data class FavoriteChannel(
    @PrimaryKey val streamId: String,
    val name: String,
    val url: String,
    val group: String
)

@Dao
interface PlaylistDao {
    @Query("SELECT * FROM SavedPlaylist") suspend fun getAll(): List<SavedPlaylist>
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insert(p: SavedPlaylist)
    @Query("DELETE FROM SavedPlaylist WHERE id=:id") suspend fun delete(id: Int)
    @Query("SELECT * FROM FavoriteChannel") suspend fun getFavorites(): List<FavoriteChannel>
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun addFav(f: FavoriteChannel)
    @Query("DELETE FROM FavoriteChannel WHERE streamId=:id") suspend fun removeFav(id: String)
}

@Database(entities = [SavedPlaylist::class, FavoriteChannel::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun playlistDao(): PlaylistDao
}
