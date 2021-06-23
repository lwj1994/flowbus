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
	        // android
	        implementation 'com.github.lwj1994.flowbus:flowbus-android:tag'
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
