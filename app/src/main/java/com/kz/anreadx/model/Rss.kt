package com.kz.anreadx.model

import com.gitlab.mvysny.konsumexml.Konsumer

data class Rss(val channel: Channel) {
    companion object {
        fun xml(k: Konsumer): Rss {
            k.checkCurrent("rss")
            return Rss(k.child("channel") { Channel.xml(this) })
        }
    }
}
