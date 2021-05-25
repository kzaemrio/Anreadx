package com.kz.anreadx.model

import com.gitlab.mvysny.konsumexml.Konsumer

data class Channel(val title: String, val link: String, val feedList: List<Feed>) {
    companion object {
        fun xml(k: Konsumer): Channel {
            k.checkCurrent("channel")
            return Channel(
                k.childText("title").apply {
                    k.child("language") { skipContents() }
                    k.child("pubDate") { skipContents() }
                    k.child("generator") { skipContents() }
                    k.child("description") { skipContents() }
                },
                k.childText("link"),
                k.children("item") { Feed.xml(this) }
            )
        }
    }
}
