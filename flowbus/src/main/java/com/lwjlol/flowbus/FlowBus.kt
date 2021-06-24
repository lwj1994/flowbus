package com.lwjlol.flowbus

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * EventBus implemented by [MutableSharedFlow]
 * support normal event and sticky event (cache 1 event)
 *
 * usage:
 *
 *     // receive event in scope
 *     scope.launch{
 *       FlowBus.observe<Event>(sticky = false){
 *          //
 *       }
 *     }
 *
 *
 *     // send event
 *     FlowBus.post(event, sticky = false)
 *
 *     // clear
 *     FlowBus.clear()
 *
 */
object FlowBus {
    private var busScope = CoroutineScope(Dispatchers.IO)

    @JvmStatic
    fun init(scope: CoroutineScope) {
        busScope = scope
    }

    @InternalFlowBusApi
    val flow = MutableSharedFlow<Bus?>(0)

    @InternalFlowBusApi
    val stickyFlow = MutableSharedFlow<Bus?>(1)

    /**
     * @param event
     * @param sticky if true, will use stickyFlow cache 1 event
     */
    fun post(event: Any, sticky: Boolean = false) = busScope.launch {
        if (sticky) {
            stickyFlow.emit(Bus(clazz = event::class.java, event = event))
        } else {
            flow.emit(Bus(clazz = event::class.java, event = event))
        }
    }

    /**
     * @param sticky
     * @param block
     */
    suspend inline fun <reified T> observe(
        sticky: Boolean = false,
        crossinline block: (T) -> Unit
    ) {
        if (sticky) {
            stickyFlow.observeInternal(block)
        } else {
            flow.observeInternal(block)
        }
    }

    @InternalFlowBusApi
    suspend inline fun <reified T> Flow<Bus?>.observeInternal(crossinline block: (T) -> Unit) {
        collect { bus ->
            bus ?: return@collect
            if (bus.clazz == T::class.java) {
                block(bus.event as T)
            }
        }
    }

    fun clear() {
        flow.tryEmit(null)
        stickyFlow.tryEmit(null)
    }

    @InternalFlowBusApi
    data class Bus(val clazz: Class<*>, val event: Any? = null)
}

@RequiresOptIn(
    level = RequiresOptIn.Level.ERROR,
    message = "This is an internal FlowBus API. It is not intended for external use."
)
annotation class InternalFlowBusApi
