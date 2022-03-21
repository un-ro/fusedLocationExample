package com.unero.fusedlocation.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.os.Looper
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.unero.fusedlocation.model.Locs
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class HomeViewModel : ViewModel() {
    private var _place: MutableLiveData<Locs> = MutableLiveData()
    val place get() = _place

    private var _state = MutableStateFlow("idle")
    val state: StateFlow<String> = _state.asStateFlow()

    @SuppressLint("MissingPermission")
    fun getLastLocation(context: Context){
        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

        fusedLocationProviderClient.lastLocation.addOnSuccessListener {
            val geoCoder = Geocoder(context)
            val currentLocation = geoCoder.getFromLocation(it.latitude, it.longitude, 1)

            _place.value = Locs(
                it.latitude,
                it.longitude,
                currentLocation.first().subLocality
            )
        }.addOnFailureListener {
            toastMaker(context, it.message.toString())
        }
    }

    @SuppressLint("MissingPermission")
    fun getCurrentLocation(context: Context) {
        val locationRequest = LocationRequest.create().apply {
            interval = 30000
            fastestInterval = 10000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            isWaitForAccurateLocation = true
        }

        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

        _state.value = "update"
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            object : LocationCallback(){
                override fun onLocationResult(locationResult: LocationResult) {
                    super.onLocationResult(locationResult)

                    if (locationResult.locations.size > 0) {
                        val locIndex = locationResult.locations.size - 1

                        val latitude = locationResult.locations[locIndex].latitude
                        val longitude = locationResult.locations[locIndex].longitude
                        val address = Geocoder(context).getFromLocation(latitude, longitude, 1)

                        toastMaker(
                            context,
                            "New Loc: $latitude, $longitude"
                        )
                        _place.value = Locs (latitude, longitude, address.first().subLocality)
                    }
                }
            },
            Looper.getMainLooper()
        )
    }

    private fun toastMaker(context: Context, msg: String) {
        Toast.makeText(
            context,
            msg,
            Toast.LENGTH_SHORT
        ).show()
    }
}