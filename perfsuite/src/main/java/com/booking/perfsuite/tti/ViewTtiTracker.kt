package com.booking.perfsuite.tti

import android.view.View
import androidx.annotation.UiThread
import com.booking.perfsuite.internal.doOnNextDraw

/**
 * Android View-based implementation of TTI\TTFR tracking. This class should be used with screens
 * which are rendered using Android [View] class (Activities, Fragments, Views).
 *
 * For Android Views we always should measure time until the actual draw happens
 * and [View.onDraw] is called.
 * That's why when [onScreenViewIsReady] or [onScreenIsUsable] are called, the tracker actually
 * waits until the next frame draw before finish collecting TTFR/TTI metrics.
 *
 * Technically this is a wrapper around [BaseTtiTracker] which helps to collect metrics respectively to
 * how [View] rendering works.
 * Therefore, please use [BaseTtiTracker] directly in case of using canvas drawing,
 * Jetpack Compose or any other approach which is not based on Views.
 *
 * See also [com.booking.perfsuite.tti.helpers.ActivityTtfrHelper] and
 * [com.booking.perfsuite.tti.helpers.FragmentTtfrHelper] for automatic TTFR collection
 * in Activities and Fragments.
 */
@UiThread
public class ViewTtiTracker(private val tracker: BaseTtiTracker) {

    /**
     * Call this method immediately on screen creation as early as possible
     *
     * @param screen - unique screen identifier
     */
    public fun onScreenCreated(screen: String) {
        tracker.onScreenCreated(screen)
    }

    /**
     * Call this when screen View is ready but it is not drawn yet
     *
     * @param screen - unique screen identifier
     * @param rootView - root view of the screen, metric is ready when this view is next drawn
     */
    public fun onScreenViewIsReady(screen: String, rootView: View) {
        if (tracker.isScreenEnabledForTracking(screen)) {
            rootView.doOnNextDraw { tracker.onScreenViewIsReady(screen) }
        }
    }

    /**
     * Call this when the screen View is ready for user interaction.
     * Only the first call after screen creation is considered, repeat calls are ignored
     *
     * @see BaseTtiTracker.onScreenIsUsable
     *
     * @param screen - unique screen identifier
     * @param rootView - root view of the screen, metric is ready when this view is next drawn
     *
     *
     */
    public fun onScreenIsUsable(screen: String, rootView: View) {
        if (tracker.isScreenEnabledForTracking(screen)) {
            rootView.doOnNextDraw { tracker.onScreenIsUsable(screen) }
        }
    }

    /**
     * Call this when user leaves the screen.
     *
     * This prevent us from tracking cheap screen transitions (e.g. back navigation,
     * when the screen is already created in memory), so we're able to track
     * only real screen creation performance, removing outliers
     */
    public fun onScreenStopped(screen: String) {
        tracker.onScreenStopped(screen)
    }
}