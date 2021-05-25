package com.kz.anreadx.dispatcher

import kotlinx.coroutines.*
import java.util.concurrent.Executor
import kotlin.coroutines.CoroutineContext


class DispatcherSwitch constructor(
    private val db: DB,
    private val io: IO,
    private val cpu: CPU
) {
    suspend fun <T> db(block: suspend CoroutineScope.() -> T) = withContext(db, block)
    suspend fun <T> io(block: suspend CoroutineScope.() -> T) = withContext(io, block)
    suspend fun <T> cpu(block: suspend CoroutineScope.() -> T) = withContext(cpu, block)
}

class DB constructor(executor: Executor) :
    DispatcherWrapper(executor.asCoroutineDispatcher())

class IO : DispatcherWrapper(Dispatchers.IO)

class CPU : DispatcherWrapper(Dispatchers.Default)

open class DispatcherWrapper(private val dispatcher: CoroutineDispatcher) : CoroutineDispatcher() {
    override fun dispatch(context: CoroutineContext, block: Runnable) {
        dispatcher.dispatch(context, block)
    }
}
