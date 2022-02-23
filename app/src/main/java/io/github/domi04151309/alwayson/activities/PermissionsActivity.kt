package io.github.domi04151309.alwayson.activities

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import io.github.domi04151309.alwayson.R
import io.github.domi04151309.alwayson.helpers.Root
import io.github.domi04151309.alwayson.helpers.Theme

class PermissionsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        Theme.set(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, PreferencePermissions())
                .commit()
    }

    class PreferencePermissions : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            addPreferencesFromResource(R.xml.pref_permissions)
            findPreference<Preference>("ignore_battery_optimizations")?.setOnPreferenceClickListener {
                startActivity(Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS))
                true
            }
            findPreference<Preference>("root_mode")?.setOnPreferenceClickListener {
                if (!Root.request()) {
                    val toast = Toast.makeText(context, R.string.setup_root_failed, Toast.LENGTH_LONG)
                    toast.setGravity(Gravity.CENTER, 0, 0)
                    toast.show()
                    (it as SwitchPreference).isChecked = false
                }
                true
            }
        }
    }
}