package com.hofit.greenroad.service_location.application

import android.app.Application
import com.hofit.greenroad.service_location.main_activity_maps.MyLocationManager
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class GreenroadApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this

        //init location services
        MyLocationManager.instance()
    }

    companion object{

        private var instance = GreenroadApplication()

        fun instance(): GreenroadApplication {
            return instance
        }
    }
}