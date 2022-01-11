package com.kz.anreadx.di

import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.util.concurrent.Executor

@EntryPoint
@InstallIn(SingletonComponent::class)
interface ExecutorEntryPoint {
    fun executor(): Executor
}
