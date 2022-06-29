package com.example.aoa.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.text.format.DateFormat
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.preference.PreferenceManager
import com.example.aoa.R
import com.example.aoa.activities.setup.*

class SetupActivity : AppCompatActivity() {

    private var currentFragment = DRAW_OVER_OTHER_APPS_FRAGMENT
    private var isActionRequired = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setup)

        val prefsEditor = PreferenceManager.getDefaultSharedPreferences(this).edit()
        swapContentFragment(DrawOverOtherAppsFragment(), DRAW_OVER_OTHER_APPS_FRAGMENT)

        if (DateFormat.is24HourFormat(this)) prefsEditor.putBoolean("hour", false).apply()
        else prefsEditor.putBoolean("hour", true).apply()

        findViewById<Button>(R.id.continueBtn).setOnClickListener {
            when (currentFragment) {
                NO_FRAGMENT -> {
                    swapContentFragment(DrawOverOtherAppsFragment(), DRAW_OVER_OTHER_APPS_FRAGMENT)
                }
                DRAW_OVER_OTHER_APPS_FRAGMENT -> {
                    if (Settings.canDrawOverlays(this)) {
                        swapContentFragment(PhoneStateFragment(), PHONE_STATE_FRAGMENT)
                    } else {
                        startActivityForResult(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION), 1)
                        isActionRequired = true
                    }
                }
                PHONE_STATE_FRAGMENT -> {
                    if (applicationContext.checkSelfPermission(Manifest.permission.READ_PHONE_STATE)
                        != PackageManager.PERMISSION_GRANTED
                    ) {
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(Manifest.permission.READ_PHONE_STATE),
                            0
                        )
                    } else {
                        prefsEditor.putBoolean("setup_complete", true).apply()
                        startActivity(Intent(this, MainActivity::class.java))
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (isActionRequired) {
            when (currentFragment) {
                DRAW_OVER_OTHER_APPS_FRAGMENT -> {
                    if (!Settings.canDrawOverlays(this)) {
                        Toast.makeText(this, R.string.setup_error, Toast.LENGTH_LONG).show()
                    } else {
                        swapContentFragment(PhoneStateFragment(), PHONE_STATE_FRAGMENT)
                    }
                }
            }
            isActionRequired = false
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (currentFragment > NO_FRAGMENT) currentFragment--
    }

    private fun swapContentFragment(fragment: Fragment, id: Byte) {
        currentFragment = id
        supportFragmentManager.beginTransaction()
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .replace(R.id.content, fragment, null)
            .addToBackStack(null)
            .commitAllowingStateLoss()
    }

    companion object {
        const val NO_FRAGMENT: Byte = 0
        const val DRAW_OVER_OTHER_APPS_FRAGMENT: Byte = 1
        const val PHONE_STATE_FRAGMENT: Byte = 2
    }
}
