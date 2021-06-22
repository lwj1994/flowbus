package com.lwjlol.flowbus.android

import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle.State
import androidx.lifecycle.Lifecycle.State.CREATED
import androidx.lifecycle.Lifecycle.State.STARTED
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.lwjlol.flowbus.FlowBus
import com.lwjlol.flowbus.FlowBus.Bus
import com.lwjlol.flowbus.InternalFlowBusApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * EventBus implemented by [kotlinx.coroutines.flow.MutableSharedFlow]
 * support normal event and sticky event (cache 1 event)
 *
 * usage:
 *     // receive event in lifecycleOwner
 *     FlowBus.observe<Event>(lifecycleOwner = this, state = STARTED, sticky = false){
 *          //
 *     }
 *
 *     // send event
 *     FlowBus.post(event, sticky = false)
 *
 */

/**
 * receive event when the lifecycle is at least [activeState].
 * if [dropWhenInactive] is true, will drop event when lifecycle is inactive
 * @see [repeatOnLifecycle]
 *
 * @param lifecycleOwner
 * @param activeState receive event >= [activeState]
 * @param sticky indicate whether is sticky event
 * @param dropWhenInactive drop event when [LifecycleOwner] is inactive (<=[State.DESTROYED])
 */
inline fun <reified T> FlowBus.observe(
  lifecycleOwner: LifecycleOwner,
  activeState: State = CREATED,
  dropWhenInactive: Boolean = false,
  sticky: Boolean = false,
  crossinline block: (T) -> Unit
) {
  if (sticky) {
    stickyFlow.observeInternal(lifecycleOwner, activeState, dropWhenInactive, block)
  } else {
    flow.observeInternal(lifecycleOwner, activeState, dropWhenInactive, block)
  }
}

@InternalFlowBusApi
inline fun <reified T> Flow<Bus?>.observeInternal(
  lifecycleOwner: LifecycleOwner,
  activeState: State,
  dropWhenInactive: Boolean,
  crossinline block: (T) -> Unit
) {
  (if (lifecycleOwner is Fragment && lifecycleOwner.view != null) lifecycleOwner.viewLifecycleOwner else lifecycleOwner).let {
    it.lifecycleScope.launch {
      if (!dropWhenInactive) {
        it.repeatOnLifecycle(activeState) {
          collectInternal(lifecycleOwner, activeState, false, block)
        }
      } else {
        collectInternal(lifecycleOwner, activeState, true, block)
      }
    }
  }
}

@InternalFlowBusApi
suspend inline fun <reified T> Flow<Bus?>.collectInternal(
  lifecycleOwner: LifecycleOwner,
  activeState: State,
  dropWhenInactive: Boolean,
  crossinline block: (T) -> Unit
) {
  collect { bus ->
    // inActive to drop event
    if (dropWhenInactive && !lifecycleOwner.lifecycle.currentState.isAtLeast(activeState)) {
      return@collect
    }
    bus ?: return@collect
    if (bus.clazz == T::class.java) {
      block(bus.event as T)
    }
  }
}
