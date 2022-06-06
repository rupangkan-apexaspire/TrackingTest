package com.example.trackingtest

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationRequest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.trackingtest.databinding.ActivityMapsBinding
import com.firebase.geofire.GeoFire
import com.firebase.geofire.GeoLocation
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApi
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var mGoogleApiClient: GoogleApiClient
    private lateinit var mLastLocation: Location
    private lateinit var mLocationRequest: com.google.android.gms.location.LocationRequest
    private val TAG = "ReposeMapsActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            return
        }
        buildGoogleApiClient()
        mMap.isMyLocationEnabled = true
    }

    private fun buildGoogleApiClient() {
        mGoogleApiClient = GoogleApiClient.Builder(this)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API)
            .build()
        mGoogleApiClient.connect()
    }

    override fun onLocationChanged(p0: Location) {
        mLastLocation = p0
        Log.d(TAG, "onLocationChanged: Latitude: ${mLastLocation.latitude}, Longitude: ${mLastLocation.longitude}")
        var latLong = LatLng(p0.latitude, p0.longitude)
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLong))
        mMap.animateCamera(CameraUpdateFactory.zoomTo(12F))

        var userId = FirebaseAuth.getInstance().currentUser!!.uid
        var ref = FirebaseDatabase.getInstance().getReference("driversAvailable")

        var geoFire = GeoFire(ref)
        geoFire.setLocation(userId, GeoLocation(mLastLocation.latitude, mLastLocation.longitude))
    }

    override fun onConnected(p0: Bundle?) {
        mLocationRequest = com.google.android.gms.location.LocationRequest()
            .setInterval(1000)
            .setFastestInterval(1000)
            .setPriority(com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            return
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this)

    }

    override fun onConnectionSuspended(p0: Int) {


    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        TODO("Not yet implemented")
    }

    override fun onStop() {
        super.onStop()
        var userId = FirebaseAuth.getInstance().currentUser!!.uid
        var ref = FirebaseDatabase.getInstance().getReference("driversAvailable")

        var geoFire = GeoFire(ref)
        geoFire.removeLocation(userId)
    }

}