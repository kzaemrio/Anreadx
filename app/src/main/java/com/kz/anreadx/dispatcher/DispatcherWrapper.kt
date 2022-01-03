package com.kz.anreadx.dispatcher

import kotlinx.coroutines.ExecutorCoroutineDispatcher
import kotlinx.coroutines.Runnable
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.CoroutineContext

@Singleton
class Background @Inject constructor(executor: Executor) :
    SimpleExecutorCoroutineDispatcher(executor)

abstract class SimpleExecutorCoroutineDispatcher constructor(override val executor: Executor) :
    ExecutorCoroutineDispatcher() {
    override fun close() {
        (executor as? ExecutorService)?.shutdown()
    }

    override fun dispatch(context: CoroutineContext, block: Runnable) {
        executor.execute(block)
    }
}
