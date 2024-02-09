package com.booking.perfsuite.rendering

/**
 * Class aggregating raw frame metrics into more high level representation suitable for
 * reporting and simpler to analyze
 */
public data class RenderingMetrics(

    /**
     * Total amount of frames rendered during the screen session
     */
    val totalFrames: Long,

    /**
     * Amount of frames that take more that 16ms to render (considered as "slow")
     */
    val slowFrames: Long,

    /**
     * Amount of frames that take more that 700ms to render (considered as "frozen")
     */
    val frozenFrames: Long,

    /**
     * Total time of freezing the UI due to rendering of the slow frames per screen session.
     * This metric accumulates all freeze durations during the screen session
     */
    val totalFreezeTimeMs: Long = 0,

    /**
     * Total time spent by user on current screen. Helpful as a supporting metrics, since sometimes
     * increase in Total Freeze Time might be caused by longer interactions with the screen
     */
    val foregroundTimeMs: Long? = null
)
