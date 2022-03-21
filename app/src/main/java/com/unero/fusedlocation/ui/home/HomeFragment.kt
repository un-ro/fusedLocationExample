package com.unero.fusedlocation.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.unero.fusedlocation.R
import com.unero.fusedlocation.databinding.HomeFragmentBinding
import com.unero.fusedlocation.helper.PermissionHelper
import com.unero.fusedlocation.helper.PermissionHelper.requestLocationPermission
import com.vmadalin.easypermissions.EasyPermissions
import com.vmadalin.easypermissions.dialogs.SettingsDialog

class HomeFragment : Fragment(), EasyPermissions.PermissionCallbacks {

    private var _binding: HomeFragmentBinding? = null
    private val binding get() = _binding as HomeFragmentBinding

    private lateinit var viewModel: HomeViewModel
    private lateinit var mapFragment: SupportMapFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = HomeFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment

        binding.btnUpdate.setOnClickListener { updateLocation() }
    }

    private fun updateLocation() {
        if (PermissionHelper.hasLocationPermission(requireContext())) {
            viewModel.getCurrentLocation(requireContext())
            viewModel.place.observe(viewLifecycleOwner) { loc ->
                // Update Text
                binding.tvLatitude.text = "Latitude: ${loc.latitude}"
                binding.tvLongitude.text = "Longitude: ${loc.longitude}"
                binding.tvPlace.text = "Kelurahan: ${loc.place}"

                // Update Google Map
                mapFragment.getMapAsync {
                    val currentLoc = LatLng(loc.latitude, loc.longitude)
                    it.addMarker(MarkerOptions().position(currentLoc).title("You're Here!"))
                    it.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLoc, 20f))
                }
            }
        } else {
            requestLocationPermission(requireActivity())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            SettingsDialog.Builder(requireContext()).build().show()
        } else {
            requestLocationPermission(requireActivity())
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        Toast.makeText(
            requireContext(),
            "Permission Granted",
            Toast.LENGTH_SHORT
        ).show()
    }
}