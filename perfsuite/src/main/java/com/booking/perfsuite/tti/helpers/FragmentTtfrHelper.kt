package com.booking.perfsuite.tti.helpers

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.booking.perfsuite.tti.ViewTtiTracker

/**
 * This class helps to automatically track TTFR metric for every fragment
 * within the particular activity or particular parent fragment by handling
 * [androidx.fragment.app.FragmentManager.FragmentLifecycleCallbacks]
 *
 * @param tracker TTI tracker instance
 * @param screenNameProvider function used to generate unique screen name/identifier for fragment.
 *      If it returns null, then fragment won't be tracked.
 *      By default it uses the implementation based on Fragment's class name
 */
public class FragmentTtfrHelper(
    private val tracker: ViewTtiTracker,
    private val screenNameProvider: (Fragment) -> String? = { it.javaClass.name }
) : FragmentManager.FragmentLifecycleCallbacks() {

    override fun onFragmentPreCreated(
        fm: FragmentManager,
        fragment: Fragment,
        savedInstanceState: Bundle?
    ) {
        val screenKey = screenNameProvider(fragment) ?: return
        tracker.onScreenCreated(screenKey)
    }

    override fun onFragmentStarted(fm: FragmentManager, fragment: Fragment) {
        val screenKey = screenNameProvider(fragment) ?: return
        val rootView = fragment.view ?: return
        tracker.onScreenViewIsReady(screenKey, rootView)
    }

    override fun onFragmentStopped(fm: FragmentManager, fragment: Fragment) {
        val screenKey = screenNameProvider(fragment) ?: return
        tracker.onScreenStopped(screenKey)
    }
}
