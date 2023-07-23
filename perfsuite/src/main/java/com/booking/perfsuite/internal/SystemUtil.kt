package com.booking.perfsuite.internal

import android.os.SystemClock

/**
 * Returns current time for performance measurements by relying on [SystemClock.uptimeMillis],
 * since the measurements should not be affected by what happens in a deep sleep
 *
 * @return current time in milliseconds
 */
internal fun nowMillis(): Long = SystemClock.uptimeMillis()
