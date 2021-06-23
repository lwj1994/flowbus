package com.lwjlol.flowbus

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.coroutines.EmptyCoroutineContext

class FlowBusTest {
    data class Event(val a: Int)

    val receiveScope = CoroutineScope(EmptyCoroutineContext)

    @Test
    fun normalEvent() {
        // receive event only when observe first
        FlowBus.post(Event(1))
        receiveScope.launch {
            delay(1000)
            FlowBus.observe<Event> {
                println(it.a)
                // only receive 3
                assert(it.a == 3)
            }
        }
        runBlocking {
            delay(2000)
            repeat(20){
                FlowBus.post(Event(3)).join()
            }
        }
    }

    @Test
    fun stickyEvent() {
        // receive 1 cache event
        FlowBus.post(Event(1), true)
        receiveScope.launch {
            delay(1000)
            FlowBus.observe<Event>(sticky = true) {
                println(it.a)
//                assert(it.a == 3)
            }
        }
        runBlocking {
            delay(2000)
            FlowBus.post(Event(3), true).join()
        }
    }
}
