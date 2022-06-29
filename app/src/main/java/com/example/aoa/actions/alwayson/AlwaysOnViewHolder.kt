package com.example.aoa.actions.alwayson

import android.app.Activity
import com.example.aoa.R
import com.example.aoa.custom.CustomFrameLayout
import com.example.aoa.custom.CustomImageView

class AlwaysOnViewHolder(activity: Activity) {
    val frame: CustomFrameLayout = activity.findViewById(R.id.frame)
    val customView: com.example.aoa.actions.alwayson.AlwaysOnCustomView = activity.findViewById(R.id.customView)
    val fingerprintIcn: CustomImageView = activity.findViewById(R.id.fingerprintIcn)
}