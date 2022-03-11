package com.unero.fusedlocation.helper

import android.Manifest
import android.app.Activity
import android.content.Context
import com.vmadalin.easypermissions.EasyPermissions

object PermissionHelper {

    private const val REQUEST_LOCATION_CODE = 13

    fun hasLocationPermission(context: Context) =
        EasyPermissions.hasPermissions(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

    fun requestLocationPermission(activity: Activity) {
        EasyPermissions.requestPermissions(
            activity,
            "Application need to have this permission",
            REQUEST_LOCATION_CODE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }
}