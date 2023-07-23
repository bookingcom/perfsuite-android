package com.booking.perfsuite.startup

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import android.os.Process
import com.booking.perfsuite.internal.nowMillis

/**
 * Implementation of [ContentProvider]  providing the closest possible timestamp
 * to the app's startup process.
 *
 * **Note:** By default it is declared in AndroidManifest.xml with the `android:initOrder="9999"`
 * to ensure that this content provider is initialized first and able to properly collect start time
*/
public class AppStartTimeProvider: ContentProvider() {

    public companion object {

        // Maximum limit for the time since process starts till Application.onCreate is called
        private const val MAX_APP_CREATION_TIME = 60_000L

        private var contentProviderOnCreateTime: Long = 0

        /**
         * Returns the timestamp of app's process creation (the earliest possible moment)
         *
         * @param onCreateTime timestamp of [android.app.Application.onCreate] invocation in millis
         * @return app's start time in millis
         */
        @JvmStatic
        public fun getAppStartTime(onCreateTime: Long): Long =
            Process.getStartUptimeMillis().let { processStartTime ->
                if (onCreateTime - processStartTime > MAX_APP_CREATION_TIME) {
                    contentProviderOnCreateTime
                } else {
                    processStartTime
                }
            }
    }

    override fun onCreate(): Boolean {
        contentProviderOnCreateTime = nowMillis()
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? = null

    override fun insert(uri: Uri, values: ContentValues?): Uri? = null

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int = 0

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int = 0

    override fun getType(uri: Uri): String? = null
}