package com.hofit.greenroad.service_location.repository

import android.location.Location
import android.util.Log
import com.hofit.greenroad.service_location.db.TriggerLog
import com.hofit.greenroad.service_location.db.TriggerLogsDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class RepositoryImp @Inject constructor(val dbDao : TriggerLogsDao) : IRepositoryController {

    override fun updateLocation(){
        Log.d("HofitTest", "RepositoryImp ---->>>> UpdateLocation()")
    }

    override fun loadTriggersLogsList() {
//        val response: List<TriggerLog>
        CoroutineScope(Dispatchers.IO).launch {
            var response = dbDao.getAll()
            Log.d("HofitTest", response?.isEmpty().toString())
            //update live data
        }
    }

    override fun addTriggerLocation(location : Location){
        CoroutineScope(Dispatchers.IO).launch {
            dbDao.insertAll(TriggerLog(latitude = location.latitude, longitude = location.longitude, timestamp = System.currentTimeMillis()))
        }
    }
}