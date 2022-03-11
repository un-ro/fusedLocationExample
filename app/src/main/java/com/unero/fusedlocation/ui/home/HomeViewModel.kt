package com.unero.fusedlocation.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.location.LocationServices
import com.unero.fusedlocation.model.Locs

class HomeViewModel : ViewModel() {
    private var _place: MutableLiveData<Locs> = MutableLiveData()
    val place get() = _place

    @SuppressLint("MissingPermission")
    fun getLocation(context: Context){
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
            Toast.makeText(
                context,
                it.message,
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}