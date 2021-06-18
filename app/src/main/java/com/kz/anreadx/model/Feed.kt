package com.kz.anreadx.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter

@Entity
data class Feed(
    @ColumnInfo val title: String,
    @ColumnInfo val description: String,
    @PrimaryKey val link: String,
    @ColumnInfo val pubDate: Long,
    @ColumnInfo val done: Boolean = false
)

fun Feed.feedTimeLabel(): String = formatTime("HH:mm")

fun Feed.detailTimeLabel(): String = formatTime("MMM dd, yyyy 'at' HH:mm")

private fun Feed.formatTime(pattern: String): String {
    return ZonedDateTime.ofInstant(
        Instant.ofEpochMilli(pubDate),
        ZoneId.systemDefault()
    ).format(DateTimeFormatter.ofPattern(pattern))
}
