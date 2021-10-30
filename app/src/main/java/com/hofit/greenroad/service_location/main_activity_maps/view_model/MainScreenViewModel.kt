package com.hofit.greenroad.service_location.main_activity_maps.view_model

import android.content.Context
import android.content.Intent
import android.location.Location
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hofit.greenroad.service_location.main_activity_maps.LocationManagerListener
import com.hofit.greenroad.service_location.main_activity_maps.MyLocationManager
import com.hofit.greenroad.service_location.repository.IRepositoryController
import com.hofit.greenroad.service_location.service.ForegroundService
import com.hofit.greenroad.service_location.utils.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainScreenViewModel @Inject constructor(var repositoryController: IRepositoryController) : ViewModel(){

    var livaData = MutableLiveData<String>()
    var livaDataLocation = MutableLiveData<Location>()

    fun startObserveLocationEvents(){

        MyLocationManager.instance().getUserLocationUpdates(object : LocationManagerListener{
            override fun onLocationChange(location: Location) {
                livaDataLocation.value = location
            }
        })

//
//        loadTriggersLogsList()
//
//        repositoryController.updateLocation()
//        livaData.value = "OK"
    }

    private fun loadTriggersLogsList() {
        repositoryController.loadTriggersLogsList()
    }

    fun updateTriggerLocation(lastLocation: Location) {
        repositoryController.addTriggerLocation(lastLocation)
    }

    fun checkIfNeedOpenService(context: Context) {
        if(!Utils.isServiceRunning(context, ForegroundService::class.java.name ?: "")){
            Log.d("HofitTest", "isServiceNotRunning = true");
            val intent = Intent(context, ForegroundService::class.java)
            ActivityCompat.startForegroundService(context, intent)
        }
    }
}