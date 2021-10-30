package com.hofit.greenroad.service_location.main_activity_maps

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.hofit.greenroad.service_location.application.GreenroadApplication

class MyLocationManager private constructor() {

    private val mFusedLocationClient: FusedLocationProviderClient
    private var mListener: LocationManagerListener? = null
    private val locationCallback: LocationCallback
    private val locationRequest: LocationRequest
    private val locationSettingsRequest: LocationSettingsRequest

    private var locationChange: (Location) -> Unit = { location ->
        mListener?.onLocationChange(location)
    }

    fun getUserLocationUpdates(listener: LocationManagerListener) {
        mListener = listener
    }

    fun stop() {
        Log.i(TAG, "stop() Stopping location tracking")
        mFusedLocationClient.removeLocationUpdates(locationCallback)
    }

    companion object {
        private val instance = MyLocationManager()
        var lastLocation: Location? = null
        private val TAG = MyLocationManager::class.java.simpleName
        private const val UPDATE_INTERVAL_IN_MILLISECONDS: Long = 5 * 1000
        private const val FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS: Long = 5 * 1000
        fun instance(): MyLocationManager {
            return instance
        }
    }

    init {
        locationRequest = LocationRequest()
        locationRequest.interval = UPDATE_INTERVAL_IN_MILLISECONDS
        locationRequest.fastestInterval = FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(locationRequest)
        locationSettingsRequest = builder.build()
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                val currentLocation: Location = locationResult.lastLocation

                if(lastLocation == null || lastLocation?.latitude != currentLocation.latitude && lastLocation?.longitude != currentLocation.longitude){
                    lastLocation = currentLocation
                    locationChange(currentLocation)
                    Log.i("HofitTest", "locationChange")
                }
            }
        }
        mFusedLocationClient =
            LocationServices.getFusedLocationProviderClient(GreenroadApplication.instance().applicationContext)

        if (ActivityCompat.checkSelfPermission(
                GreenroadApplication.instance().applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                GreenroadApplication.instance().applicationContext,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
        }
        mFusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback, Looper.myLooper()
        )
    }
}

interface LocationManagerListener {
    //if last location not the same
    fun onLocationChange(location: Location)
}