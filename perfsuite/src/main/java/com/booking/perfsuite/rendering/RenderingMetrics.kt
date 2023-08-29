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
     * Amount of "good" frames that potentially could be rendered, but were "dropped" due to
     * rendering performance issues.
     *
     * For instance if we target 60fps, the "good" frame would be any frame rendered for less than
     * 16ms. But when we see "slow" frame that take 48ms to render, it spend the same time to render
     * as 3 "good" frames would spend. That means that due to performance issue the app has rendered
     * 1 frame, but 2 potentially good frames were dropped
     */
    val droppedFrames: Long,

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

