package com.kz.anreadx.persistence

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kz.anreadx.model.Feed

@Dao
interface FeedDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(feeds: Iterable<Feed>)

    @Query("DELETE FROM feed WHERE pubDate < :time")
    suspend fun deleteBefore(time: Long)

    @Query("SELECT * FROM feed ORDER BY pubDate DESC")
    suspend fun list(): List<Feed>

    @Query("UPDATE feed SET done = 1")
    suspend fun readAll()

    @Query("UPDATE feed SET done = 1 WHERE link = :link")
    suspend fun read(link: String)

    @Query("SELECT * FROM feed WHERE link = :link LIMIT 1")
    suspend fun query(link: String): Feed
}
