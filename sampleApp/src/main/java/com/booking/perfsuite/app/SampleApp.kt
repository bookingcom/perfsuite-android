package com.booking.perfsuite.app

import android.app.Application
import com.booking.perfsuite.app.monitoring.ActivityFrameMetricsListener
import com.booking.perfsuite.app.monitoring.AppStartupTimeListener
import com.booking.perfsuite.rendering.ActivityFrameMetricsTracker
import com.booking.perfsuite.startup.AppStartupTimeTracker

class SampleApp : Application() {

    override fun onCreate() {
        super.onCreate()

        // setup startup time tracking
        AppStartupTimeTracker.addStartupTimeListener(this, AppStartupTimeListener)
        // setup rendering performance tracking
        ActivityFrameMetricsTracker(ActivityFrameMetricsListener).selfRegister(this)
    }
}
