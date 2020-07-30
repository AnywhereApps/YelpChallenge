package com.anywhereapps.yelp.test.ui.main.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.anywhereapps.yelp.test.R
import com.anywhereapps.yelp.test.data.model.Business
import com.anywhereapps.yelp.test.utils.showSnackbar
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_map_detail.*

class MapDetail : AppCompatActivity(), OnMapReadyCallback {

    private var business: Business? = null
    private lateinit var layout: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_detail)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        layout = findViewById(R.id.main_layout)
        business = intent?.getParcelableExtra("item")

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)


    }


    override fun onMapReady(googleMap: GoogleMap?) {
        progressBar.visibility = View.GONE
        googleMap?.setOnMarkerClickListener { marker ->
            if(marker.tag == business?.name) {
                val bottomSheetFragment = BusinessFragment()
                val bundle = Bundle()
                bundle.putParcelable("item", business)
                bottomSheetFragment.arguments = bundle
                bottomSheetFragment.show(supportFragmentManager, bottomSheetFragment.tag)
            }else{
                layout.showSnackbar(R.string.current_location, Snackbar.LENGTH_SHORT)
            }
            true
        }


        googleMap?.apply {

            val userLatitude = intent?.getDoubleExtra("latitude", 0.0)
            val userLongitude = intent?.getDoubleExtra("longitude", 0.0)
            val currentLocMarker = addMarker(MarkerOptions().position(LatLng(userLatitude!!,userLongitude!!)).icon(BitmapDescriptorFactory.defaultMarker(
                BitmapDescriptorFactory.HUE_MAGENTA)))

            val latitude = business?.coordinates!!.latitude
            val longitude = business?.coordinates!!.longitude
            val markerLocation = LatLng(latitude!!, longitude!!)
            val marker = addMarker(
                MarkerOptions()
                    .position(markerLocation)
                    .title(business?.name)
            )
            marker.tag = business?.name

            googleMap.animateCamera(
                CameraUpdateFactory.newLatLngZoom(markerLocation, 10.5f),
                5000,
                null
            )
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}