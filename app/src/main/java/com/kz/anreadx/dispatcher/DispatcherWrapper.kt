package com.kz.anreadx.dispatcher

import kotlinx.coroutines.*
import java.util.concurrent.Executor
import kotlin.coroutines.CoroutineContext

class DB constructor(executor: Executor) :
    DispatcherWrapper(executor.asCoroutineDispatcher())

class IO : DispatcherWrapper(Dispatchers.IO)

class CPU : DispatcherWrapper(Dispatchers.Default)

open class DispatcherWrapper(private val dispatcher: CoroutineDispatcher) : CoroutineDispatcher() {
    override fun dispatch(context: CoroutineContext, block: Runnable) {
        dispatcher.dispatch(context, block)
    }
}
