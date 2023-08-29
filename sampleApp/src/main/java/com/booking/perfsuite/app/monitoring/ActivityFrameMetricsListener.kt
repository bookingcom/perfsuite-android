package com.booking.perfsuite.app.monitoring

import android.app.Activity
import android.util.Log
import android.util.SparseIntArray
import com.booking.perfsuite.rendering.ActivityFrameMetricsTracker
import com.booking.perfsuite.rendering.RenderingMetricsMapper

internal object ActivityFrameMetricsListener : ActivityFrameMetricsTracker.Listener {

    override fun onFramesMetricsReady(
        activity: Activity,
        frameMetrics: Array<SparseIntArray>,
        foregroundTime: Long?
    ) {
        val activityName = activity.javaClass.simpleName
        val data = RenderingMetricsMapper.toRenderingMetrics(frameMetrics, foregroundTime) ?: return

        Log.d("PerfSuite", "Frame metrics for [$activityName] are collected: $data")
    }
}