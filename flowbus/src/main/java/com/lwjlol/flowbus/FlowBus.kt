package com.lwjlol.flowbus

import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * EventBus implemented by [MutableSharedFlow]
 * support normal event and sticky event (cache 1 event)
 *
 * usage:
 *     // receive event in lifecycleOwner
 *     FlowBus.observe<Event>(lifecycleOwner = this, sticky = false){
 *          //
 *     }
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
 */
object FlowBus {
    private val busScope = CoroutineScope(Dispatchers.Main.immediate)

    @InternalFlowBusApi
    val flow = MutableSharedFlow<Bus?>(0)

    @InternalFlowBusApi
    val stickyFlow = MutableSharedFlow<Bus?>(1)

    /**
     * @param event
     * @param sticky if true, will use stickyFlow cache 1 event
     */
    fun post(event: Any, sticky: Boolean = false) {
        busScope.launch {
            if (sticky) {
                stickyFlow.emit(Bus(clazz = event::class.java, event = event))
            } else {
                flow.emit(Bus(clazz = event::class.java, event = event))
            }
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

    inline fun <reified T> observe(
        lifecycleOwner: LifecycleOwner,
        sticky: Boolean = false,
        crossinline block: (T) -> Unit
    ) {
        if (sticky) {
            stickyFlow.observeInternal(lifecycleOwner, block)
        } else {
            flow.observeInternal(lifecycleOwner, block)
        }
    }

    @InternalFlowBusApi
    inline fun <reified T> Flow<Bus?>.observeInternal(
        lifecycleOwner: LifecycleOwner,
        crossinline block: (T) -> Unit
    ) {
        (if (lifecycleOwner is Fragment && lifecycleOwner.view != null) lifecycleOwner.viewLifecycleOwner else lifecycleOwner).lifecycleScope.launchWhenCreated {
            observeInternal(block)
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
