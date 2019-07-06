package io.github.domi04151309.alwayson.charging

import android.app.ActivityManager
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.widget.ImageView
import io.github.domi04151309.alwayson.Global
import io.github.domi04151309.alwayson.R

class Flash : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_charging_flash)

        Global.fullscreen(this, findViewById(R.id.chargingLayout))

        val animationThread = object : Thread() {
            override fun run() {
                try {
                    sleep(1500)
                    val image = findViewById<ImageView>(R.id.chargingImage)
                    image.animate().alpha(0f).duration = 1000
                    sleep(1000)
                    Global.close(this@Flash)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }

            }
        }
        animationThread.start()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return true
    }

    override fun onPause() {
        super.onPause()
        val activityManager = applicationContext
                .getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        activityManager.moveTaskToFront(taskId, 0)
    }
}
