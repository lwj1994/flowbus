# FlowBus
![](https://jitpack.io/v/lwj1994/flowbus.svg)

FlowVersion EventBus


## Usage
Add it in your root build.gradle at the end of repositories:
```gradle
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}


	dependencies {
	        // core
	        implementation 'com.github.lwj1994.flowbus:flowbus:tag'
	        implementation "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.0"

	        // android
	        implementation 'com.github.lwj1994.flowbus:flowbus-android:tag'
	        implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.3.1")
            implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.4.0-alpha02")
	}
```


```kotlin
       // receive event in scope
       receiveScope.launch {
            delay(1000)
            FlowBus.observe<Event> {
                println(it.a)
                // only receive 3
                assert(it.a == 3)
            }
       }

       // post event
       FlowBus.post(Event(1))
```

## Android

```kotlin
        // receive event bind lifecycleOwner
        FlowBus.observeLifecycle<Event>(lifecycleOwner) {

        }
```
