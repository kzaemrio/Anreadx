package com.kz.anreadx.persistence

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kz.anreadx.model.LastPosition

@Dao
interface LastPositionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(lastPosition: LastPosition)

    @Query("SELECT * FROM LastPosition LIMIT 1")
    suspend fun query(): LastPosition?
}
