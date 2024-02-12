package com.booking.perfsuite.tti

import androidx.annotation.UiThread
import com.booking.perfsuite.internal.nowMillis

/**
 * The most basic TTFR/TTI tracking implementation.
 * This class can be used with any possible screen implementation
 * (Activities, Fragments, Views, Jetpack Compose and etc.).
 *
 * To work properly it requires that methods are called respectively to the screen lifecycle events:
 * 1. Call [onScreenCreated] at the earliest possible moment of the screen instantiation
 * 2. Then call [onScreenViewIsReady] when the first screen frame is shown to the user,
 *    that will indicate that TTFR metric is collected
 * 3. Optionally call [onScreenIsUsable] when the usable content is shown to the user,
 *    that will indicate that TTI metric is collected
 *
 * For more details please refer to the documentation:
 * https://github.com/bookingcom/perfsuite-android?tab=readme-ov-file#additional-documentation
 *
 * @param listener implementation is used to handle screen TTI\TTFR metrics when they are ready
 */
@UiThread
public class BaseTtiTracker(
    private val listener: Listener
) {

    private val screenCreationTimestamp = HashMap<String, Long>()

    /**
     * Call this method immediately on screen creation as early as possible
     *
     * @param screen - unique screen identifier
     * @param timestamp - the time the screen was created at.
     */
    public fun onScreenCreated(screen: String, timestamp: Long = nowMillis()) {
        screenCreationTimestamp[screen] = timestamp
        listener.onScreenCreated(screen)
    }

    /**
     * Call this method when screen is rendered for the first time
     *
     * @param screen - unique screen identifier
     */
    public fun onScreenViewIsReady(screen: String) {
        screenCreationTimestamp[screen]?.let { creationTimestamp ->
            val duration = nowMillis() - creationTimestamp
            listener.onFirstFrameIsDrawn(screen, duration)
        }
    }

    /**
     * Call this method when the screen is ready for user interaction
     * (e.g. all data is ready and meaningful content is shown).
     *
     * The method is optional, whenever it is not called TTI won't be measured
     *
     * @param screen - unique screen identifier
     */
    public fun onScreenIsUsable(screen: String) {
        screenCreationTimestamp[screen]?.let { creationTimestamp ->
            val duration = nowMillis() - creationTimestamp
            listener.onFirstUsableFrameIsDrawn(screen, duration)
            screenCreationTimestamp.remove(screen)
        }
    }

    /**
     * Call this when user leaves the screen.
     *
     * This prevent us from producing outliers and avoid tracking cheap screen transitions
     * (e.g. back navigation, when the screen is already created in memory),
     * so we're able to track only real screen creation performance
     */
    public fun onScreenStopped(screen: String) {
        screenCreationTimestamp.remove(screen)
    }

    /**
     * Returns true if the screen is still in the state of collecting metrics.
     * When result is false,that means that both TTFR/TTI metrics were already collected or
     * discarded for any reason
     */
    public fun isScreenEnabledForTracking(screen: String): Boolean =
        screenCreationTimestamp.containsKey(screen)

    /**
     * Listener interface providing TTFR/TTI metrics when they're ready
     */
    public interface Listener {

        /**
         * Called as early as possible after the screen [screen] is created.
         *
         * @param screen - screen key
         */
        public fun onScreenCreated(screen: String)

        /**
         * Called when the very first screen frame is drawn
         *
         * @param screen - screen key
         * @param duration - elapsed time since screen's creation till the first frame is drawn
         */
        public fun onFirstFrameIsDrawn(screen: String, duration: Long)

        /**
         * Called when the first usable/meaningful screen frame is drawn
         *
         * @param screen - screen key
         * @param duration - elapsed time since screen's creation till the usable frame is drawn
         */
        public fun onFirstUsableFrameIsDrawn(screen: String, duration: Long)
    }
}