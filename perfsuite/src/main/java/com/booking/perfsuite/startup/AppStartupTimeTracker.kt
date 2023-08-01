package com.booking.perfsuite.startup

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.annotation.UiThread
import com.booking.perfsuite.internal.doOnFirstDraw
import com.booking.perfsuite.internal.isForegroundProcess
import com.booking.perfsuite.internal.nowMillis

/**
 * This class is responsible for app startup tracking & measures the time since the earliest
 * possible moment of the app's process creation till the app's first frame is drawn.
 *
 * Startup time is measured according to the Google's definition of
 * [Cold App Startup Time](https://developer.android.com/topic/performance/vitals/launch-time)
 */
public object AppStartupTimeTracker {

    /**
     * Register a callback invoked when the app's cold startup time is obtained.
     * The method must be called as early as possible from [Application.onCreate].
     *
     * @param application current [Application] instance
     * @param listener callback to be invoked as soon as startup time is ready
     */
    @JvmStatic
    public fun register(application: Application, listener: Listener) {
        if(!isForegroundProcess()) return

        val appOnCreateTime = nowMillis()
        val appStartTime = AppStartTimeProvider.getAppStartTime(appOnCreateTime)

        application.registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {

            private var isInterrupted = false

            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                activity.doOnFirstDraw {
                    if (!isInterrupted) {
                        application.unregisterActivityLifecycleCallbacks(this)

                        val duration = nowMillis() - appStartTime
                        val isActualColdStart = savedInstanceState == null
                        listener.onColdStartupTimeIsReady(duration, activity, isActualColdStart)
                    }
                }
            }

            override fun onActivityPaused(activity: Activity) {
                // Drop the tracking if the activity paused even before its first frame is drawn
                isInterrupted = true
            }
            override fun onActivityStarted(activity: Activity) {}
            override fun onActivityDestroyed(activity: Activity) {}
            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
            override fun onActivityStopped(activity: Activity) {}
            override fun onActivityResumed(activity: Activity) {}

        })
    }

    /**
     * Listener to be invoked when the app's startup time is ready
     */
    public interface Listener {

        /**
         * Called when the app cold startup time is ready
         *
         * @param startupTime app' cold startup time in milliseconds
         * @param firstActivity instance of the first activity launched after the app starts
         * @param isActualColdStart the flag informs if the real cold start was detected. If the flag
         *  is false, then the activity is created with a saved instance state bundle, which mean that
         *  it shouldn't be considered as purely cold start, but rather as a one of warm start scenarios
         */
        @UiThread
        public fun onColdStartupTimeIsReady(startupTime: Long, firstActivity: Activity, isActualColdStart: Boolean)
    }
}
