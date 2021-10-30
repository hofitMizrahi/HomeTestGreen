package com.hofit.greenroad.service_location.main_activity_maps.view

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.hofit.greenroad.R
import com.hofit.greenroad.service_location.main_activity_maps.view_model.MainScreenViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainScreenActivity : AppCompatActivity(), OnMapReadyCallback {

    private val viewModel: MainScreenViewModel by viewModels()

    private var requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission is granted. Continue the action or workflow in your
            viewModel.startObserveLocationEvents()
            viewModel.checkIfNeedOpenService(this)
        } else {
            //show popup to open settings
        }
    }

    private var mGoogleMap: GoogleMap? = null
    private var currentLocationMarker: Marker? = null
    private var triggerMarker: Marker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initObservers()

        //set observers to view model
        //use view binding
        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.map_fragment) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
    }

    private fun initObservers() {
        viewModel.livaData.observe(this, { s ->
            Log.i("HofitTest", "log from observeble $s")
        })
        viewModel.livaDataLocation.observe(this, { location ->
            addCurrentLocationMarker(LatLng(location.latitude, location.longitude))
        })
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        //setup map
        mGoogleMap = googleMap
//        mGoogleMap?.setOnMapClickListener(this)
        val location = LatLng(-33.852, 151.211)

//        addMarker(location)

        if(!checkPermission()){
            askPermission()
        }else{
            viewModel.startObserveLocationEvents()
            viewModel.checkIfNeedOpenService(this)
        }
    }

    private fun addCurrentLocationMarker(location: LatLng) {
        mGoogleMap?.let {

            currentLocationMarker?.remove()
            currentLocationMarker = it.addMarker(
                MarkerOptions().position(location).title("My Location")
            )
            it.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 12.0f))
        }
    }

//    override fun onMapClick(location: LatLng?) {
//        location?.let {
//            addMarker(it)
//        }
//    }

    private fun checkPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this, ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun askPermission() {
        Log.i("HofitTest", "askPermission()");
        requestPermissionLauncher.launch(ACCESS_FINE_LOCATION)
    }

    // Defined in mili seconds.
    // This number in extremely low, and should be used only for debug
    private fun showGeofenceCircleInMap(
        context: Context,
        radius: Double,
        location: LatLng
    ) {
        val vectorToBitmap = vectorToBitmap(
            context.resources,
            R.drawable.common_google_signin_btn_icon_dark
        )
        addCurrentLocationMarker(location)
        mGoogleMap?.addCircle(
            CircleOptions()
                .center(location)
                .radius(radius)
                .strokeColor(ContextCompat.getColor(context, R.color.black))
                .fillColor(ContextCompat.getColor(context, R.color.white))
        )
        mGoogleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15.0f))
    }

    private fun vectorToBitmap(resources: Resources, icon: Int): BitmapDescriptor? {
        val vectorDrawable = ResourcesCompat.getDrawable(resources, icon, null)
        val bitmap = Bitmap.createBitmap(
            vectorDrawable!!.intrinsicWidth,
            vectorDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }
}
