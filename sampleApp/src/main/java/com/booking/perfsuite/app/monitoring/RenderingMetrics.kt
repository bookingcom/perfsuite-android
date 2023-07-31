package com.booking.perfsuite.app.monitoring

internal data class RenderingMetrics internal constructor(
    val total: Long,
    val slow: Long,
    val frozen: Long,
    val dropped: Long,
    val totalFreezeTimeMs: Long = 0,
    val foregroundTimeMs: Long? = null
)

