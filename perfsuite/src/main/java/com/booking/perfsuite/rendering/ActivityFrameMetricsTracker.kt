package com.booking.perfsuite.rendering

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.SparseIntArray
import androidx.annotation.UiThread
import androidx.core.app.FrameMetricsAggregator
import com.booking.perfsuite.internal.nowMillis
import java.util.WeakHashMap

/**
 * Implementation of frames metric tracking based on
 * [android.app.Application.ActivityLifecycleCallbacks]
 * which automatically collects frame metrics for every Activity in the app
 */
@UiThread
public class ActivityFrameMetricsTracker(
    private val listener: Listener
) : Application.ActivityLifecycleCallbacks {

    private val aggregator = FrameMetricsAggregator()
    private val activityStartTimes = WeakHashMap<Activity, Long>()

    /**
     * Registers current [ActivityFrameMetricsTracker] instance with the app as
     * [android.app.Application.ActivityLifecycleCallbacks].
     *
     * Call this method at the app startup, before the first activity is created
     */
    public fun selfRegister(application: Application) {
        application.registerActivityLifecycleCallbacks(this)
    }

    override fun onActivityStarted(activity: Activity) {
        aggregator.add(activity)
        activityStartTimes[activity] = nowMillis()
    }

    override fun onActivityPaused(activity: Activity) {
        val metrics = aggregator.reset()
        if (metrics != null) {
            val foregroundTime = activityStartTimes.remove(activity)
                ?.let { nowMillis() - it }
            listener.onFramesMetricsReady(activity, metrics, foregroundTime)
        }
    }

    override fun onActivityStopped(activity: Activity) {
        try {
            aggregator.remove(activity)
        } catch (ignored: Exception) {
            // do nothing, aggregator.remove() may cause rare crashes on some devices
        }
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
    override fun onActivityResumed(activity: Activity) {}
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
    override fun onActivityDestroyed(activity: Activity) {}

    /**
     * Listener interface providing notifications when the activity's frame metrics are ready
     */
    public interface Listener {

        /**
         * Called everytime when foreground activity goes to the "paused" state,
         * which means that frame metrics for this screen session are collected
         *
         * @param activity current activity
         * @param frameMetrics raw frame metrics collected by [FrameMetricsAggregator]
         * @param foregroundTime time in millis, spent by this activity in foreground state
         */
        public fun onFramesMetricsReady(
            activity: Activity,
            frameMetrics: Array<SparseIntArray>,
            foregroundTime: Long?
        )
    }
}
