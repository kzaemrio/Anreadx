package com.kz.anreadx.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class LastPosition(
    @ColumnInfo val link: String,
    @ColumnInfo val offset: Int,
    @PrimaryKey val id: String = "LastPosition"
)
