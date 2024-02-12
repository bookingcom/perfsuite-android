package com.booking.perfsuite.app.monitoring

import android.util.Log
import com.booking.perfsuite.tti.BaseTtiTracker

object AppTtiListener : BaseTtiTracker.Listener {

    override fun onScreenCreated(screen: String) {}

    override fun onFirstFrameIsDrawn(screen: String, duration: Long) {
        Log.d("PerfSuite", "$screen - TTFR = ${duration}ms")
    }

    override fun onFirstUsableFrameIsDrawn(screen: String, duration: Long) {
        Log.d("PerfSuite", "$screen - TTI = ${duration}ms")
    }
}