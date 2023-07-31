package com.booking.perfsuite.app.monitoring

import android.util.SparseIntArray
import androidx.core.app.FrameMetricsAggregator
import androidx.core.util.forEach
import androidx.core.util.isEmpty

internal object RenderingMetricsMapper {

    fun toRenderingMetrics(
        metrics: Array<SparseIntArray>?,
        foregroundTime: Long?
    ): RenderingMetrics? {
        val totalMetrics = metrics?.getOrNull(FrameMetricsAggregator.TOTAL_INDEX)

        if (totalMetrics == null || totalMetrics.isEmpty()) return null

        var total = 0L
        var slow = 0L
        var frozen = 0L
        var dropped = 0L
        var totalFreezeTime = 0L

        totalMetrics.forEach { frameDuration, numberOfFrames ->
            if (!isValidDuration(frameDuration)) return@forEach

            if (frameDuration > FROZEN_FRAME_THRESHOLD_MS) {
                frozen += numberOfFrames.toLong()
            }
            if (frameDuration > SLOW_FRAME_THRESHOLD_MS) {
                slow += numberOfFrames.toLong()
                totalFreezeTime += frameDuration * numberOfFrames
            }
            total += numberOfFrames.toLong()

            dropped += (frameDuration / SLOW_FRAME_THRESHOLD_MS) * numberOfFrames
        }

        return RenderingMetrics(
            total = total,
            slow = slow,
            frozen = frozen,
            dropped = dropped,
            totalFreezeTimeMs = totalFreezeTime,
            foregroundTimeMs = foregroundTime
        )
    }

    /**
     * Due to potential bug in [FrameMetricsAggregator] in combination with
     * unsafe int to long cast there (see FrameMetricsApi24Impl.addDurationItem(..))
     * it produces sometimes non-realistic frames durations such as negatives and
     * extremely high values close to [Integer.MAX_VALUE].
     *
     * Since it's barely possible to identify and fix the issue inside the SDK we
     * exclude such frame durations from rendering reports
     */
    private fun isValidDuration(frameDuration: Int): Boolean =
        frameDuration in 0 until MAX_FRAME_DURATION_MS

    /**
     * All frames that takes >16ms are considered as "slow"
     *
     * For more details: https://support.google.com/googleplay/android-developer/answer/7385505
     */
    private const val SLOW_FRAME_THRESHOLD_MS = 16

    /**
     * All frames that takes >700ms are considered as "frozen"
     *
     * For more details: https://support.google.com/googleplay/android-developer/answer/7385505
     */
    private const val FROZEN_FRAME_THRESHOLD_MS = 700

    /**
     * All frames that takes >5s are considered as invalid and excluded from frames report
     * It should be safe to discard this data, since freezes longer than 5s are considered as ANRs
     */
    private const val MAX_FRAME_DURATION_MS = 5_000

}
