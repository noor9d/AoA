package com.example.aoa.actions

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.pm.ActivityInfo
import android.media.AudioManager
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.widget.Toast
import androidx.preference.PreferenceManager
import com.example.aoa.R
import com.example.aoa.helpers.Global
import com.example.aoa.helpers.Root
import com.example.aoa.receivers.AdminReceiver
import java.lang.Exception

@SuppressLint("Registered")
open class OffActivity : Activity() {

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        requestedOrientation = when (PreferenceManager.getDefaultSharedPreferences(this)
            .getString("orientation", "locked")) {
            "portrait" -> ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            "landscape" -> ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            else -> ActivityInfo.SCREEN_ORIENTATION_LOCKED
        }
        super.onCreate(savedInstanceState)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_VOLUME_UP -> {
                (getSystemService(Context.AUDIO_SERVICE) as AudioManager)
                    .adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, 0)
            }
            KeyEvent.KEYCODE_VOLUME_DOWN -> {
                (getSystemService(Context.AUDIO_SERVICE) as AudioManager)
                    .adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, 0)
            }
        }
        return true
    }

    override fun onPause() {
        super.onPause()
        try {
            (getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager)
                .moveTaskToFront(taskId, 0)
        } catch (e: Exception) {
            Log.w(Global.LOG_TAG, e.toString())
        }
    }

    open fun finishAndOff() {
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("root_mode", false)) {
            Root.shell("input keyevent KEYCODE_POWER")
        } else {
            val policyManager = getSystemService(Context.DEVICE_POLICY_SERVICE)
                    as DevicePolicyManager
            if (policyManager.isAdminActive(ComponentName(this, AdminReceiver::class.java))) {
                policyManager.lockNow()
            } else {
                Toast.makeText(this, R.string.pref_admin_summary, Toast.LENGTH_SHORT).show()
            }
        }
        finish()
    }
}