package com.booking.perfsuite.internal

import android.app.Activity
import android.view.View
import android.view.ViewTreeObserver
import android.view.Window
import androidx.annotation.UiThread
import androidx.core.view.doOnAttach

/**
 * Performs the given action when the activity very first frame is drawn.
 *
 * This function can be called multiple times on the Same activity, and all the submitted actions
 * will be called.
 */
@UiThread
public fun Activity.doOnFirstDraw(action: () -> Unit) {
    window.onDecorViewReady { decorView ->
        decorView.doOnNextDraw {
            decorView.handler.postAtFrontOfQueue(action) // wait till the draw finished
        }
    }
}

/**
 * Performs the given action when the view is next drawn.
 *
 * The action will only be invoked once on the next draw and then removed.
 */
@UiThread
public fun View.doOnNextDraw(action: () -> Unit) {
    if (!viewTreeObserver.isAlive) return
    doOnAttach {
        viewTreeObserver.addOnDrawListener(
            NextDrawListener(this, action)
        )
    }
}

/**
 * Performs the given action when the decor view of the current Window is ready
 */
private fun Window.onDecorViewReady(action: (decorView: View) -> Unit) {
    val decorView = peekDecorView()
    if (decorView == null) {
        doOnNextContentChanged { action(this.decorView) }
    } else {
        action(decorView)
    }
}

/**
 * Performs the given one-time action when [Window] content changes next time.
 *
 * This method adds one-shot [Window.Callback] wrapper, which restores the original
 * [Window.Callback] right before the callback is invoked for the first time
 */
private fun Window.doOnNextContentChanged(action: () -> Unit) {
    callbackWrapper().addAction(action)
}

private fun Window.callbackWrapper(): WindowCallbackWrapper {
    return when (val originalCallback = callback) {
        is WindowCallbackWrapper -> originalCallback
        else -> WindowCallbackWrapper(originalCallback).also {
            // Inject custom wrapper which will propagate actions to nested Window.Callbacks.
            // No need to reset this later, since any following call will just append its action
            // to this instance and the action is removed right after being triggered
            callback = it
        }
    }
}

private class NextDrawListener(
    val view: View,
    val callback: () -> Unit
) : ViewTreeObserver.OnDrawListener {

    private var isInvoked = false
    private var viewTreeObserver = view.viewTreeObserver

    override fun onDraw() {
        if (isInvoked) return
        isInvoked = true

        callback()

        view.post {
            if (viewTreeObserver.isAlive) {
                viewTreeObserver.removeOnDrawListener(this)
            }
        }
    }
}

@UiThread
private class WindowCallbackWrapper constructor(
    private val originalCallback: Window.Callback
) : Window.Callback by originalCallback {

    private val onContentChangedCallbacks = mutableListOf<() -> Unit>()

    override fun onContentChanged() {
        onContentChangedCallbacks.forEach { it.invoke() }
        // clear the callbacks since they only one-shot callbacks are supported here
        onContentChangedCallbacks.clear()
        originalCallback.onContentChanged()
    }

    fun addAction(action: () -> Unit) {
        onContentChangedCallbacks += action
    }
}