package com.example.tryingsomething.ui.main

import android.app.Application
import android.location.Location
import androidx.lifecycle.LiveData
import com.example.tryingsomething.dto.LocationDetails
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices

class LocationLiveData(application: Application): LiveData<LocationDetails>() {

    private var fusedLocationClient = LocationServices.getFusedLocationProviderClient(application)

    override fun onActive() {
        super.onActive()
        fusedLocationClient.lastLocation.addOnSuccessListener {
            location: Location -> location.also {
                setLocationData(it)
        }
        }
        startLocationUpdates()
    }

    private fun startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }

    override fun onInactive() {
        super.onInactive()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
    private val locationCallback = object : LocationCallback(){
        override fun onLocationResult(p0: LocationResult) {
            super.onLocationResult(p0)
            p0 ?: return
            for (location in p0.locations) {
                setLocationData(location)
            }

        }
    }

    private fun setLocationData(location: Location) {
        value = LocationDetails(location.latitude.toString(), location.latitude.toString())
    }

    companion object {
        val ONE_MINUTE: Long = 60000
        val locationRequest: LocationRequest = LocationRequest.create().apply {
            interval = ONE_MINUTE
            fastestInterval = ONE_MINUTE/4
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }

}
