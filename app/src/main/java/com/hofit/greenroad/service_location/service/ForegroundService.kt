package com.hofit.greenroad.service_location.service

import android.Manifest
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.PRIORITY_MIN
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.Geofence.GEOFENCE_TRANSITION_ENTER
import com.google.android.gms.location.Geofence.GEOFENCE_TRANSITION_EXIT
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.hofit.greenroad.R
import com.hofit.greenroad.service_location.main_activity_maps.LocationManagerListener
import com.hofit.greenroad.service_location.main_activity_maps.MyLocationManager
import com.hofit.greenroad.service_location.repository.IRepositoryController
import com.hofit.greenroad.service_location.repository.RepositoryImp
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ForegroundService : Service(), GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener,
    ResultCallback<Status> {

    @Inject
    lateinit var myRepository: IRepositoryController

    companion object {
        const val NOTIFICATION_NAME = "My Foreground Service"
        const val NOTIFICATION_NAME_ID = "my_service"
        const val NOTIFICATION_ID = 101
    }

    private lateinit var geofencingClient: GeofencingClient
    private var mGoogleApiClient: GoogleApiClient? = null

    private fun addGeofence(geoRequest: GeofencingRequest) {

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        LocationServices.GeofencingApi.addGeofences(
            mGoogleApiClient,
            geoRequest,
            getGeofencePendingIntent()
        ).setResultCallback(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        //need to call in five sec - if not -> crash

        startForeground(NOTIFICATION_ID, getNotification())

        //if use START_STICKY -> intent will be null when service restart by the os
        //this restart and deliver the same intent
        return START_STICKY
    }

    private fun startGeofance() {

        CoroutineScope(Dispatchers.Default).launch {
            while (MyLocationManager.lastLocation == null) {
                delay(2000)
                Log.i("HofitTest", "locaion =========== null")
            }
            Log.i("HofitTest", "locaion != null")
            val geo = createGeofence(
                MyLocationManager.lastLocation?.latitude ?: 0.0,
                MyLocationManager.lastLocation?.longitude ?: 0.0,
                300f
            )
            val geoRequest = createGeoRequest(geo)
            addGeofence(geoRequest)
        }
    }

    override fun onCreate() {
        super.onCreate()
        geofencingClient = LocationServices.getGeofencingClient(this)
        createGoogleApi()
        MyLocationManager.instance().getUserLocationUpdates(object : LocationManagerListener {
            override fun onLocationChange(location: Location) {
//                livaDataLocation.value = location
//                MyLocationManager.instance()
            }
        })
    }

    private fun createGoogleApi() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build()
        }
        mGoogleApiClient?.connect()
    }

    private var mGeofencePendingIntent : PendingIntent? = null

    private fun getGeofencePendingIntent(): PendingIntent? {
        // Re-use the Pending Intent if it already exists
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent
        }

        // The intent for the IntentService to receive the transitions
        val intent = Intent(this, GeofenceBroadcastReceiver::class.java)

        // Create the pending intent
        mGeofencePendingIntent = PendingIntent
            .getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        return mGeofencePendingIntent
    }

    private fun createGeoRequest(geo: Geofence): GeofencingRequest {
        return GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_EXIT)
            .addGeofence(geo)
            .build()
    }

    private fun createGeofence(lat: Double, lng: Double, region: Float): Geofence {
        return Geofence.Builder().setCircularRegion(lat, lng, region)
            .setRequestId("myGeofence")
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .setTransitionTypes(GEOFENCE_TRANSITION_ENTER or GEOFENCE_TRANSITION_EXIT)
            .build()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    // service notification
    private fun getNotification(): Notification {
        val channelId =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createNotificationChannel()
            } else {
                // If earlier version channel ID is not used
                ""
            }

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
        return notificationBuilder.setOngoing(true)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setPriority(PRIORITY_MIN)
            .setCategory(Notification.CATEGORY_SERVICE)
            .build()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(): String {
        val chan = NotificationChannel(
            NOTIFICATION_NAME_ID,
            NOTIFICATION_NAME, NotificationManager.IMPORTANCE_NONE
        )
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(chan)
        return NOTIFICATION_NAME_ID
    }

    override fun onConnected(p0: Bundle?) {
        //success
        Log.i("HofitTest", "onConnected - apiClient")
        startGeofance()
    }

    override fun onConnectionSuspended(p0: Int) {
        Log.i("HofitTest", "onConnectionSuspended - apiClient")
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        Log.i("HofitTest", "onConnectionFailed - apiClient")
    }

    override fun onResult(trigger: Status) {
        Log.d("HofitTest", "trigger onResult() = " + trigger);

        when(trigger){

        }
    }
}



