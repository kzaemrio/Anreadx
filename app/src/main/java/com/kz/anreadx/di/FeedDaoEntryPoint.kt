package com.kz.anreadx.di

import com.kz.anreadx.persistence.FeedDao
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@EntryPoint
@InstallIn(SingletonComponent::class)
interface FeedDaoEntryPoint {
    fun feedDao(): FeedDao
}