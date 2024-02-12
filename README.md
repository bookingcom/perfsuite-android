# PerformanceSuite Android

[![CI status](https://github.com/bookingcom/perfsuite-android/actions/workflows/ci.yml/badge.svg)](https://github.com/bookingcom/perfsuite-android/actions/workflows/ci.yml)
[![License](https://img.shields.io/badge/License-Apache_2.0-green.svg)](https://github.com/bookingcom/perfsuite-android/blob/main/LICENSE)
[![Release version](https://img.shields.io/github/release/bookingcom/perfsuite-android.svg?label=Release)](https://github.com/bookingcom/perfsuite-android/releases)

Lightweight library designed to measure and collect performance metrics for Android applications 
in production.

Unlike other SaaS solutions (like Firebase Performance) it focuses only on collecting pure metrics 
and not enforces you to use specific reporting channel and monitoring infrastructure, so you're 
flexible with re-using the monitoring approaches already existing in your product.

## Getting started

Library supports collecting following performance metrics:
- App Cold Startup Time
- Rendering performance per Activity
- Time to Interactive & Time to First Render per screen

We recommend to read our blogpost ["Measuring mobile apps performance in production"](https://medium.com/booking-com-development/measuring-mobile-apps-performance-in-production-726e7e84072f) 
first to get some idea on what are these performance metrics, how they work and why those were chosen.

> NOTE: You can also refer to the [SampleApp](sampleApp/src/main/java/com/booking/perfsuite/app) 
> in this repo to see a simplified example of how the library can be used in the real app

### Dependency

The library is available on Maven Central:
```groovy
implementation("com.booking:perfsuite:0.3")
```

### Collecting Startup Times

Implement the callback invoked once [Startup Time](https://medium.com/booking-com-development/measuring-mobile-apps-performance-in-production-726e7e84072f#e383) is collected:

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
(we can call it "the end of the screen session") and use `RenderingMetricsMapper` to
represent [rendering performance metrics](https://medium.com/booking-com-development/measuring-mobile-apps-performance-in-production-726e7e84072f#3eca) 
in a convenient aggregated format:

```kotlin
class MyFrameMetricsListener : ActivityFrameMetricsTracker.Listener {

    override fun onFramesMetricsReady(
        activity: Activity,
        frameMetrics: Array<SparseIntArray>,
        foregroundTime: Long?
    ) {
        val data = RenderingMetricsMapper.toRenderingMetrics(frameMetrics, foregroundTime) ?: return
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

As per the code sample above you can use `RenderingMetricsMapper` to collect frames metics in the aggreated format which is convenient for reporting to the backend.
Then metrics will be represented as [`RenderingMetrics`](src/main/java/com/booking/perfsuite/rendering/RenderingMetrics.kt) instance, which will provide data on:
- `totalFrames` - total amount of frames rendered during the screen session
- `totalFreezeTimeMs` - total accumulated time of the UI being frozen during the screen session
- `slowFrames` - amount of [slow frames](https://firebase.google.com/docs/perf-mon/screen-traces?platform=android#slow-rendering-frames) per screens session
- `frozenFrames` - amount of [frozen frames](https://firebase.google.com/docs/perf-mon/screen-traces?platform=android#frozen-frames) per screens session

Even though we support collecting widely used slow & frozen frames we [strongly recommend relying on `totalFreezeTimeMs` as the main rendering metric](https://medium.com/booking-com-development/measuring-mobile-apps-performance-in-production-726e7e84072f#2d5d)

### Collecting Screen Time to Interactive (TTI)

Implement the callbacks invoked every time when screen's
[Time To Interactive (TTI)](https://medium.com/booking-com-development/measuring-mobile-apps-performance-in-production-726e7e84072f#ad4d) &
[Time To First Render (TTFR)](https://medium.com/booking-com-development/measuring-mobile-apps-performance-in-production-726e7e84072f#f862)
metrics are collected:

```kotlin
object MyTtiListener : BaseTtiTracker.Listener {

    override fun onScreenCreated(screen: String) {}

    override fun onFirstFrameIsDrawn(screen: String, duration: Long) {
        // Log or report TTFR metrics for specific screen in a preferable way
    }
    override fun onFirstUsableFrameIsDrawn(screen: String, duration: Long) {
        // Log or report TTI metrics for specific screen in a preferable way
    }
}
```

Then instantiate TTI tracker in `Application#onCreate` before any activity is created and using this listener:

```kotlin
// keep instances globally accessible or inject as singletons using any preferable DI framework
val ttiTracker = BaseTtiTracker(AppTtiListener)
val viewTtiTracker = ViewTtiTracker(ttiTracker)

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        ActivityTtfrHelper.register(this, viewTtiTracker)
    }
}
```

That will enable automatic TTFR collection for every Activity in the app.
For TTI collection you'll need to call `viewTtiTracker.onScreenIsUsable(..)` manually from the Activity, 
when the meaningful data is visible to the user e.g.:

```kotlin
// call this e.g. when the data is received from the backend,
// progress bar stops spinning and screen is fully ready for the user
viewTtiTracker.onScreenIsUsable(activity.componentName, rootContentView)
```

See the [SampleApp](sampleApp/src/main/java/com/booking/perfsuite/app) for a full working example

## Additional documentation
- [Measuring mobile apps performance in production](https://medium.com/booking-com-development/measuring-mobile-apps-performance-in-production-726e7e84072f) 
- [App Startup Time documentation by Google](https://developer.android.com/topic/performance/vitals/launch-time)
- [Rendering Performance documentation by Google](https://developer.android.com/topic/performance/vitals/render)
- [Android Vitals Articles by Pierre Yves Ricau](https://dev.to/pyricau/series/7827)
  
## ACKNOWLEDGMENT

This software was originally developed at Booking.com. 
With approval from Booking.com, this software was released as open source, 
for which the authors would like to express their gratitude.
