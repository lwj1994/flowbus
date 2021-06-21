package com.lwjlol.flowbus.sample

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.lwjlol.flowbus.FlowBus
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    val sharedFlow = MutableSharedFlow<Event>(1)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        lifecycleScope.launchWhenCreated {
            delay(200)
            FlowBus.observe<Event>(lifecycleOwner = this@MainActivity, sticky = true) {
                findViewById<TextView>(R.id.text).text = it.s
            }
        }


        lifecycleScope.launch {
            FlowBus.post(Event("sticky"), sticky = true)
        }
    }
}

data class Event(val s: String)