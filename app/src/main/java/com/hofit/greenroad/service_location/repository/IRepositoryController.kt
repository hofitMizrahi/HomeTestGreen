package com.hofit.greenroad.service_location.repository

import android.location.Location

interface IRepositoryController {
    fun updateLocation()
    fun loadTriggersLogsList()
    fun addTriggerLocation(location: Location)
}