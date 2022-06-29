package com.example.aoa.receivers

import android.content.*
import android.icu.util.Calendar
import androidx.preference.PreferenceManager
import com.example.aoa.actions.TurnOnScreenActivity
import com.example.aoa.actions.alwayson.AlwaysOn
import com.example.aoa.helpers.Rules

class CombinedServiceReceiver : BroadcastReceiver() {

    companion object {
        var isScreenOn: Boolean = true
        var isAlwaysOnRunning: Boolean = false
        var hasRequestedStop: Boolean = false
    }

    override fun onReceive(c: Context, intent: Intent) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(c)
        val rules = Rules(c, prefs)

        when (intent.action) {
            Intent.ACTION_POWER_CONNECTED -> {
                if (rules.isAlwaysOnDisplayEnabled()
                    && !rules.isAmbientMode()
                    && rules.matchesBatteryPercentage()
                    && rules.isInTimePeriod(Calendar.getInstance())
                    && !isScreenOn
                ) {
                    c.startActivity(
                        Intent(
                            c,
                            AlwaysOn::class.java
                        ).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    )
                }
            }
            Intent.ACTION_POWER_DISCONNECTED -> {
                if (rules.isAlwaysOnDisplayEnabled()
                    && !rules.isAmbientMode()
                    && rules.matchesBatteryPercentage()
                    && rules.isInTimePeriod(Calendar.getInstance())
                    && !isScreenOn
                ) {
                    c.startActivity(
                        Intent(
                            c,
                            AlwaysOn::class.java
                        ).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    )
                }
            }
            Intent.ACTION_SCREEN_OFF -> {
                isScreenOn = false
                val alwaysOn = prefs.getBoolean("always_on", false)
                if (alwaysOn && !hasRequestedStop) {
                    if (isAlwaysOnRunning) {
                        c.startActivity(
                            Intent(
                                c,
                                TurnOnScreenActivity::class.java
                            ).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        )
                        isAlwaysOnRunning = false
                    } else if (!rules.isAmbientMode()
                        && rules.matchesChargingState()
                        && rules.matchesBatteryPercentage()
                        && rules.isInTimePeriod(Calendar.getInstance())
                    ) {
                        c.startActivity(
                            Intent(
                                c,
                                AlwaysOn::class.java
                            ).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        )
                    }
                } else if (alwaysOn && hasRequestedStop) {
                    hasRequestedStop = false
                    isAlwaysOnRunning = false
                }
            }
            Intent.ACTION_SCREEN_ON -> {
                isScreenOn = true
            }
        }
    }
}
