package com.kz.anreadx.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.gitlab.mvysny.konsumexml.Konsumer
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
) {
    companion object {
        fun xml(k: Konsumer): Feed {
            k.checkCurrent("item")
            return Feed(
                k.childText("title"),
                k.childText("description"),
                k.childText("link").apply {
                    k.child("guid") { skipContents() }
                },
                toLong(k.childText("pubDate")),
                false,
            )
        }

        private fun toLong(trim: String): Long {
            val originalZonedDateTime = getZonedDateTime(trim)
            val fixedZonedDateTime =
                originalZonedDateTime.withZoneSameInstant(ZoneId.systemDefault())
            return fixedZonedDateTime.toInstant().toEpochMilli()
        }

        private fun getZonedDateTime(time: String): ZonedDateTime {
            return try {
                ZonedDateTime.parse(time, DateTimeFormatter.ISO_DATE)
            } catch (e: Exception) {
                ZonedDateTime.parse(time, DateTimeFormatter.RFC_1123_DATE_TIME)
            }
        }
    }
}

fun Feed.feedTimeLabel(): String = formatTime("HH:mm")

fun Feed.detailTimeLabel(): String = formatTime("MMM dd, yyyy 'at' HH:mm")

private fun Feed.formatTime(pattern: String): String {
    return org.threeten.bp.ZonedDateTime.ofInstant(
        org.threeten.bp.Instant.ofEpochMilli(pubDate),
        org.threeten.bp.ZoneId.systemDefault()
    ).format(org.threeten.bp.format.DateTimeFormatter.ofPattern(pattern))
}