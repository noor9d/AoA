package com.example.aoa.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.service.quicksettings.TileService
import android.view.KeyEvent
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.example.aoa.R
import com.example.aoa.services.AlwaysOnTileService
import com.example.aoa.helpers.Global
import com.example.aoa.helpers.Permissions
import com.example.aoa.helpers.Theme
import com.example.aoa.services.ForegroundService

class MainActivity : AppCompatActivity() {

    enum class Dialogs {
        DEVICE_ADMIN,
        NOTIFICATION_ACCESS,
        DISPLAY_OVER_OTHER_APPS,
        PHONE_STATE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Theme.set(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        val actionBar = supportActionBar ?: return
        actionBar.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        actionBar.setDisplayShowCustomEnabled(true)
        actionBar.setCustomView(R.layout.action_bar)
        actionBar.elevation = 0f

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings, GeneralPreferenceFragment())
            .commit()

        ContextCompat.startForegroundService(this, Intent(this, ForegroundService::class.java))
    }

    override fun onStart() {
        super.onStart()

        if (Permissions.needsNotificationPermissions(this)) buildDialog(Dialogs.NOTIFICATION_ACCESS)
        if (Permissions.needsDeviceAdminOrRoot(this)) buildDialog(Dialogs.DEVICE_ADMIN)

        if (!Permissions.hasPhoneStatePermission(this)) buildDialog(Dialogs.PHONE_STATE)
        if (!Settings.canDrawOverlays(this)) buildDialog(Dialogs.DISPLAY_OVER_OTHER_APPS)
    }

    @SuppressLint("InflateParams")
    private fun buildDialog(dialogType: Dialogs) {
        val builder = AlertDialog.Builder(this)
        val view = layoutInflater.inflate(R.layout.dialog_permission, null, false)
        val icon = view.findViewById<ImageView>(R.id.icon)
        val title = view.findViewById<TextView>(R.id.title)
        val message = view.findViewById<TextView>(R.id.message)

        when (dialogType) {
            Dialogs.DEVICE_ADMIN -> {
                icon.setImageResource(R.drawable.ic_color_mode)
                title.setText(R.string.device_admin)
                message.setText(R.string.device_admin_summary)
                builder.setPositiveButton(resources.getString(android.R.string.ok)) { _, _ ->
                    startActivity(Intent(this, PermissionsActivity::class.java))
                }
            }
            Dialogs.DISPLAY_OVER_OTHER_APPS -> {
                icon.setImageResource(R.drawable.ic_color_draw_over_other_apps)
                title.setText(R.string.setup_draw_over_other_apps)
                message.setText(R.string.setup_draw_over_other_apps_summary)
                builder.setPositiveButton(resources.getString(android.R.string.ok)) { _, _ ->
                    startActivityForResult(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION), 1)
                }
            }
            Dialogs.NOTIFICATION_ACCESS -> {
                icon.setImageResource(R.drawable.ic_color_notification)
                title.setText(R.string.notification_listener_service)
                message.setText(R.string.notification_listener_service_summary)
                builder.setPositiveButton(resources.getString(android.R.string.ok)) { _, _ ->
                    startActivity(Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"))
                }
            }
            Dialogs.PHONE_STATE -> {
                icon.setImageResource(R.drawable.ic_color_phone_state)
                title.setText(R.string.setup_phone_state)
                message.setText(R.string.setup_phone_state_summary)
                builder.setPositiveButton(resources.getString(android.R.string.ok)) { _, _ ->
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.READ_PHONE_STATE),
                        0
                    )
                }
            }
        }
        builder.setView(view)
        builder.setNegativeButton(resources.getString(android.R.string.cancel)) { dialog, _ -> dialog.cancel() }
        builder.show()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onBackPressed()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onBackPressed() {
        startActivity(
            Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }

    class GeneralPreferenceFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            addPreferencesFromResource(R.xml.pref_main)
            findPreference<Preference>("always_on")?.setOnPreferenceClickListener {
                TileService.requestListeningState(
                    context,
                    ComponentName(requireContext(), AlwaysOnTileService::class.java)
                )
                LocalBroadcastManager.getInstance(requireContext())
                    .sendBroadcast(Intent().setAction(Global.ALWAYS_ON_STATE_CHANGED))
                true
            }
            findPreference<Preference>("pref_watch_face")?.setOnPreferenceClickListener {
                startActivity(Intent(context, LAFWatchFaceActivity::class.java))
                true
            }
            findPreference<Preference>("pref_background")?.setOnPreferenceClickListener {
                startActivity(Intent(context, LAFBackgroundActivity::class.java))
                true
            }
            findPreference<Preference>("rules")?.setOnPreferenceClickListener {
                startActivity(Intent(context, LAFRulesActivity::class.java))
                true
            }
            findPreference<Preference>("pref_behavior")?.setOnPreferenceClickListener {
                startActivity(Intent(context, LAFBehaviorActivity::class.java))
                true
            }
            findPreference<Preference>("dark_mode")?.setOnPreferenceClickListener {
                activity?.recreate()
                true
            }
            findPreference<Preference>("pref_permissions")?.setOnPreferenceClickListener {
                startActivity(Intent(context, PermissionsActivity::class.java))
                true
            }
            findPreference<Preference>("pref_help")?.setOnPreferenceClickListener {
                startActivity(Intent(context, HelpActivity::class.java))
                true
            }
            findPreference<Preference>("pref_about")?.setOnPreferenceClickListener {
                startActivity(Intent(context, AboutActivity::class.java))
                true
            }
        }
    }
}
