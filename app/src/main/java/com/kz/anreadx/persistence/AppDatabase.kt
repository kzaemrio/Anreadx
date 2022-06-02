package com.kz.anreadx.persistence

import androidx.room.Database
import androidx.room.RoomDatabase
import com.kz.anreadx.model.Feed

@Database(entities = [Feed::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun itemDao(): FeedDao
}
