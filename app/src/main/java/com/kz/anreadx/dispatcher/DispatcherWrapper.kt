package com.kz.anreadx.dispatcher

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.Executor
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.CoroutineContext

@Singleton
class DB @Inject constructor(executor: Executor) :
    DispatcherWrapper(executor.asCoroutineDispatcher())

@Singleton
class IO @Inject constructor() : DispatcherWrapper(Dispatchers.IO)

@Singleton
class CPU @Inject constructor() : DispatcherWrapper(Dispatchers.Default)

open class DispatcherWrapper(private val dispatcher: CoroutineDispatcher) : CoroutineDispatcher() {
    override fun dispatch(context: CoroutineContext, block: Runnable) {
        dispatcher.dispatch(context, block)
    }
}
