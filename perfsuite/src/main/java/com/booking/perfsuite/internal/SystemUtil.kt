package com.booking.perfsuite.internal

import android.app.ActivityManager
import android.os.SystemClock

/**
 * Returns current time for performance measurements by relying on [SystemClock.uptimeMillis],
 * since the measurements should not be affected by what happens in a deep sleep
 *
 * @return current time in milliseconds
 */
internal fun nowMillis(): Long = SystemClock.uptimeMillis()

/**
 * Detects if the process is currently in "foreground" state by checking
 * [android.app.ActivityManager.RunningAppProcessInfo.importance]
 *
 * @return `true` if the process is currently  in "foreground" state
 */
internal fun isForegroundProcess(): Boolean {
    val processInfo = ActivityManager.RunningAppProcessInfo()
    ActivityManager.getMyMemoryState(processInfo)
    return processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
}