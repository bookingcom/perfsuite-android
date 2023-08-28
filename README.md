# PerformanceSuite Android

[![CI status](https://github.com/bookingcom/perfsuite-android/actions/workflows/ci.yml/badge.svg)](https://github.com/bookingcom/perfsuite-android/actions/workflows/ci.yml)
[![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](https://github.com/bookingcom/perfsuite-android/blob/main/LICENSE)

Lightweight library designed to measure and collect performance metrics for Android applications 
in production.

Unlike other SaaS solutions (like Firebase Performance) it focuses only on collecting pure metrics 
and not enforces you to use specific reporting channel and monitoring infrastructure, so you're 
flexible with re-using the monitoring approaches already existing in your product.

## Getting started

Library supports collecting following performance metrics:
- App Cold Startup Time
- Rendering performance per Activity

### Dependency

The library is available on Maven Central:
```groovy
implementation("com.booking:perfsuite:0.1")
```

### Collecting Startup Times

Implement the callback invoked once Startup Time is collected:

```kotlin
class MyStartupTimeListener : AppStartupTimeTracker.Listener {

    override fun onColdStartupTimeIsReady(
        startupTime: Long,
        firstActivity: Activity,
        isActualColdStart: Boolean
    ) {
        // Log or report Startup Time metric in a preferable way
    }
}
```

Then register your listener as early in `Application#onCreate` as possible:

```kotlin
class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        AppStartupTimeTracker.register(this, MyStartupTimeListener())
    }
}
```

### Collecting Frame Metrics

Implement the callback invoked every time when the foreground `Activity` is paused 
(we can call it "the end of the screen session"):

```kotlin
class MyFrameMetricsListener : ActivityFrameMetricsTracker.Listener {

    override fun onFramesMetricsReady(
        activity: Activity,
        frameMetrics: Array<SparseIntArray>,
        foregroundTime: Long?
    ) {
        // Log or report Frame Metrics for current Activity's "session" in a preferable way
    }
}
```

Then register your listener in `Application#onCreate` before any activity is created:

```kotlin
class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        ActivityFrameMetricsTracker.register(this, MyFrameMetricsListener)
    }
}
```

## Additional documentation
- [App Startup Time documentation by Google](https://developer.android.com/topic/performance/vitals/launch-time)
- [Rendering Performance documentation by Google](https://developer.android.com/topic/performance/vitals/render)
- [Android Vitals Articles by Pierre Yves Ricau](https://dev.to/pyricau/series/7827)
  
## ACKNOWLEDGMENT

This software was originally developed at Booking.com. 
With approval from Booking.com, this software was released as open source, 
for which the authors would like to express their gratitude.
