package com.example.aoa.helpers

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import androidx.preference.PreferenceManager
import android.service.quicksettings.TileService
import com.example.aoa.services.AlwaysOnTileService
import androidx.localbroadcastmanager.content.LocalBroadcastManager

internal object Global {

    const val LOG_TAG: String = "AlwaysOn"

    const val REQUEST_DETAILED_NOTIFICATIONS: String = "com.example.aoa.REQUEST_DETAILED_NOTIFICATIONS"
    const val DETAILED_NOTIFICATIONS: String = "com.example.aoa.DETAILED_NOTIFICATIONS"
    const val REQUEST_NOTIFICATIONS: String = "com.example.aoa.REQUEST_NOTIFICATIONS"
    const val NOTIFICATIONS: String = "com.example.aoa.NOTIFICATIONS"

    const val REQUEST_STOP: String = "com.example.aoa.REQUEST_STOP"

    const val ALWAYS_ON_STATE_CHANGED: String = "com.example.aoa.ALWAYS_ON_STATE_CHANGED"

    fun currentAlwaysOnState(context: Context): Boolean {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("always_on", false)
    }

    fun changeAlwaysOnState(context: Context): Boolean {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val value = !prefs.getBoolean("always_on", false)
        prefs.edit().putBoolean("always_on", value).apply()
        TileService.requestListeningState(context, ComponentName(context, AlwaysOnTileService::class.java))
        LocalBroadcastManager.getInstance(context).sendBroadcast(Intent().setAction(ALWAYS_ON_STATE_CHANGED))
        return value
    }
}