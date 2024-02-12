package com.booking.perfsuite.app

import android.app.Activity
import android.app.Application
import android.view.View
import com.booking.perfsuite.app.monitoring.ActivityFrameMetricsListener
import com.booking.perfsuite.app.monitoring.AppStartupTimeListener
import com.booking.perfsuite.app.monitoring.AppTtiListener
import com.booking.perfsuite.rendering.ActivityFrameMetricsTracker
import com.booking.perfsuite.startup.AppStartupTimeTracker
import com.booking.perfsuite.tti.BaseTtiTracker
import com.booking.perfsuite.tti.ViewTtiTracker
import com.booking.perfsuite.tti.helpers.ActivityTtfrHelper

class SampleApp : Application() {

    override fun onCreate() {
        super.onCreate()

        // setup startup time tracking
        AppStartupTimeTracker.register(this, AppStartupTimeListener)

        // setup rendering performance tracking
        ActivityFrameMetricsTracker.register(this, ActivityFrameMetricsListener)

        // setup Activity TTI tracking
        ActivityTtfrHelper.register(this, viewTtiTracker)
    }
}

val ttiTracker = BaseTtiTracker(AppTtiListener)
val viewTtiTracker = ViewTtiTracker(ttiTracker)

fun Activity.reportIsUsable(contentView: View = this.window.decorView) {
    viewTtiTracker.onScreenIsUsable(this.javaClass.name, contentView)
}
