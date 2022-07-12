package com.kz.anreadx.di

import android.content.Context
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import java.util.concurrent.Executor

@EntryPoint
@InstallIn(SingletonComponent::class)
interface ExecutorEntryPoint {
    fun executor(): Executor
}

inline val Context.diExecutor: Executor
    get() = EntryPointAccessors.fromApplication<ExecutorEntryPoint>(applicationContext).executor()
