package com.booking.perfsuite.app.monitoring

import android.app.Activity
import android.util.Log
import com.booking.perfsuite.startup.AppStartupTimeTracker

internal object AppStartupTimeListener : AppStartupTimeTracker.Listener {

    override fun onColdStartupTimeIsReady(
        startupTime: Long,
        firstActivity: Activity,
        isActualColdStart: Boolean
    ) {
        Log.d("PerfSuite", "Startup time = ${startupTime}ms")
    }
}