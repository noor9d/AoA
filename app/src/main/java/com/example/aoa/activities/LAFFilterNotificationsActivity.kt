package com.example.aoa.activities

import android.content.*
import android.content.pm.PackageManager
import android.os.Bundle
import android.service.notification.StatusBarNotification
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.preference.Preference
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceFragmentCompat
import com.example.aoa.R
import com.example.aoa.helpers.Global
import com.example.aoa.helpers.JSON
import com.example.aoa.helpers.Theme
import org.json.JSONArray
import java.lang.Exception

class LAFFilterNotificationsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        Theme.set(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, PreferenceFragment())
                .commit()
    }

    class PreferenceFragment : PreferenceFragmentCompat() {

        internal var blockedArray: JSONArray = JSONArray()
        private lateinit var blocked: PreferenceCategory
        internal lateinit var shown: PreferenceCategory
        private lateinit var packageManager: PackageManager
        private lateinit var empty: Preference

        private val notificationReceiver = object : BroadcastReceiver() {

            override fun onReceive(c: Context, intent: Intent) {
                shown.removeAll()
                val notifications = intent.getParcelableArrayExtra("notifications") ?: arrayOf()
                val apps: ArrayList<String> = ArrayList(notifications.size)
                var pref: Preference
                notifications.forEach {
                    val notification = it as StatusBarNotification
                    if (!apps.contains(notification.packageName)) {
                        apps += notification.packageName
                        pref = generatePref(notification.packageName)
                        pref.setOnPreferenceClickListener {
                            if (!JSON.contains(blockedArray, notification.packageName)) {
                                addToList(notification.packageName)
                                blockedArray.put(notification.packageName)
                            }
                            true
                        }
                        shown.addPreference(pref)
                    }
                }
            }
        }

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            addPreferencesFromResource(R.xml.pref_laf_wf_filter_notifications)
            blocked = findPreference("blocked") ?: return
            shown = findPreference("shown") ?: return
            packageManager = requireContext().packageManager
            empty = Preference(preferenceScreen.context)
            empty.setIcon(R.drawable.ic_notification)
            empty.title = requireContext().resources.getString(R.string.pref_look_and_feel_filter_notifications_empty)
            empty.summary = requireContext().resources.getString(R.string.pref_look_and_feel_filter_notifications_empty_summary)
        }

        override fun onStart() {
            super.onStart()
            blockedArray = JSONArray(preferenceManager.sharedPreferences!!.getString("blocked_notifications", "[]"))
            if (!JSON.isEmpty(blockedArray)) {
                blocked.removeAll()
                for (i in 0 until blockedArray.length()) {
                    addToList(blockedArray.getString(i))
                }
            }

            val localManager = LocalBroadcastManager.getInstance(requireContext())
            localManager.registerReceiver(notificationReceiver, IntentFilter(Global.DETAILED_NOTIFICATIONS))
            localManager.sendBroadcast(Intent(Global.REQUEST_DETAILED_NOTIFICATIONS))
        }

        override fun onStop() {
            super.onStop()
            preferenceManager.sharedPreferences!!.edit().putString("blocked_notifications", blockedArray.toString()).apply()
        }

        internal fun addToList(packageName: String) {
            if (JSON.isEmpty(blockedArray)) blocked.removeAll()
            val pref = generatePref(packageName)
            pref.setOnPreferenceClickListener {
                JSON.remove(blockedArray, packageName)
                blocked.removePreference(it)
                if (JSON.isEmpty(blockedArray)) blocked.addPreference(empty)
                true
            }
            blocked.addPreference(pref)
        }

        internal fun generatePref(packageName: String): Preference {
            val pref = Preference(preferenceScreen.context)
            pref.setIcon(R.drawable.ic_notification)
            pref.title = try {
                packageManager.getApplicationLabel(packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)) as String
            } catch (e: Exception) {
                resources.getString(R.string.pref_look_and_feel_filter_notifications_unknown)
            }
            pref.summary = packageName
            return pref
        }
    }
}
