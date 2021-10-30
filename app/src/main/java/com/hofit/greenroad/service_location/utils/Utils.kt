package com.hofit.greenroad.service_location.utils

import android.app.ActivityManager
import android.content.Context
import androidx.appcompat.app.AppCompatActivity

class Utils {

    companion object{

        fun isServiceRunning(context: Context, serviceName: String): Boolean {
            var serviceRunning = false
            val am = context.getSystemService(AppCompatActivity.ACTIVITY_SERVICE) as ActivityManager
            val l = am.getRunningServices(50)
            val i: Iterator<ActivityManager.RunningServiceInfo> = l.iterator()
            while (i.hasNext()) {
                val runningServiceInfo = i
                    .next()
                if (runningServiceInfo.service.className == serviceName) {
                    serviceRunning = true
//                if (runningServiceInfo.foreground) {
//                    //service run in foreground
//                }
                }
            }
            return serviceRunning
        }


    }
}