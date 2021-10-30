package com.hofit.greenroad.service_location.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class GeofenceBroadcastReceiver : BroadcastReceiver() {
    // ...
    override fun onReceive(context: Context?, intent: Intent?) {
        val geofencingEvent = GeofencingEvent.fromIntent(intent)

//        CoroutineScope(Dispatchers.Default).launch {
//            repeat(100){
//                Log.d("HofitTest", "repeat broadcast lives")
//                delay(10000)
//            }
//        }

        if (geofencingEvent.hasError()) {
//            val errorMessage = GeofenceStatusCodes
//                .getStatusCodeString(geofencingEvent.errorCode)
            return
        }

        // Get the transition type.
        val geofenceTransition = geofencingEvent.geofenceTransition

        // Test that the reported transition was of interest.
        if ((geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) or
            (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT)
        ) {
            Log.d("HofitTest", "GEOFENCE_TRANSITION_ENTER")


//                if(geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER){
//                    Log.d("HofitTest", "GEOFENCE_TRANSITION_ENTER")
//                }else if(geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT){
//                    Log.d("HofitTest", "GEOFENCE_TRANSITION_EXIT")
//                }
        }
    }
}