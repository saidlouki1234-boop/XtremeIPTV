
package com.xtreme.iptv
import android.app.Application
import androidx.room.Room
import com.xtreme.iptv.data.local.AppDatabase

class XtremeApp : Application() {
    lateinit var db: AppDatabase
    override fun onCreate() {
        super.onCreate()
        db = Room.databaseBuilder(this, AppDatabase::class.java, "xtreme.db").fallbackToDestructiveMigration().build()
    }
}
