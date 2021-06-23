package com.lwjlol.flowbus.sample

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle.State
import androidx.lifecycle.lifecycleScope
import com.lwjlol.flowbus.FlowBus
import com.lwjlol.flowbus.android.observeLifecycle
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    val sharedFlow = MutableSharedFlow<Event>(1)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        FlowBus.post(Event("sticky"), true)

        FlowBus.observeLifecycle<Event>(
            lifecycleOwner = this@MainActivity,
            activeState = State.RESUMED,
            dropWhenInactive = true,
            sticky = true
        ) { e ->
            findViewById<TextView>(R.id.text).let {
                it.text = it.text.toString() + "\n" + e.s
            }
        }

        lifecycleScope.launch {
            delay(200)
            check(lifecycle.currentState.isAtLeast(State.RESUMED))
            FlowBus.post(Event("sticky2"), true)
        }
    }

    override fun onPause() {
        super.onPause()
    }
}

data class Event(val s: String)
