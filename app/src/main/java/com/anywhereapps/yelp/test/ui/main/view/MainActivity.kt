package com.anywhereapps.yelp.test.ui.main.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.anywhereapps.yelp.test.R
import com.anywhereapps.yelp.test.data.api.ApiHelper
import com.anywhereapps.yelp.test.data.api.RetrofitBuilder
import com.anywhereapps.yelp.test.data.model.Business
import com.anywhereapps.yelp.test.ui.base.ViewModelFactory
import com.anywhereapps.yelp.test.ui.main.adapter.BusinessAdapter
import com.anywhereapps.yelp.test.ui.main.viewmodel.MainViewModel
import com.anywhereapps.yelp.test.utils.Status.ERROR
import com.anywhereapps.yelp.test.utils.Status.LOADING
import com.anywhereapps.yelp.test.utils.Status.SUCCESS
import com.anywhereapps.yelp.test.utils.checkSelfPermissionCompat
import com.anywhereapps.yelp.test.utils.requestPermissionsCompat
import com.anywhereapps.yelp.test.utils.shouldShowRequestPermissionRationaleCompat
import com.anywhereapps.yelp.test.utils.showSnackbar
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*

const val PERMISSION_REQUEST_LOCATION = 0

class MainActivity : AppCompatActivity(), ActivityCompat.OnRequestPermissionsResultCallback {

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var layout: View
    private lateinit var viewModel: MainViewModel
    private lateinit var adapter: BusinessAdapter

    // Added Default lat lng for results
    private var mLatitude = 36.114647
    private var mLongitude = -115.172813


    private lateinit var miles : Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        layout = findViewById(R.id.main_layout)
        setupViewModel()
        setupUI()

    }

    override fun onResume() {
        super.onResume()
        checkPermission()
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(
                ApiHelper(
                    RetrofitBuilder.apiService
                )
            )
        ).get(MainViewModel::class.java)
    }

    private fun setupUI() {

        // recylerview setup
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = BusinessAdapter(arrayListOf()){ item ->
            goToDetailPage(item, mLatitude, mLongitude)
            }

        recyclerView.addItemDecoration(
            DividerItemDecoration(
                recyclerView.context,
                (recyclerView.layoutManager as LinearLayoutManager).orientation
            )
        )
        recyclerView.adapter = adapter

        // spinner setup
        miles = resources.getStringArray(R.array.miles)
        val adapter = ArrayAdapter(this,
            android.R.layout.simple_spinner_dropdown_item, miles)
        miles_spinner.adapter = adapter


        // search setup
        submitButton.setOnClickListener(View.OnClickListener {
            searchResult()
        })
        searchTxt.onDone {  searchResult() }

    }

    private fun searchResult(){
        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(searchTxt.windowToken, 0)

        val term = searchTxt.text.toString()
        var radiusInMills = miles.get(miles_spinner.selectedItemPosition)
        val radiusInMeter = 1600 * radiusInMills.toInt()
        getData(RetrofitBuilder.TOKEN, term, mLatitude, mLongitude, radiusInMeter.toString())
    }


    fun EditText.onDone(callback: () -> Unit) {
        setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                callback.invoke()
                true
            }
            false
        }
    }


    private fun getData(token: String, term : String, latitude : Double, longitude : Double, radius : String){

        viewModel.getEstablishments(token, term, latitude, longitude, radius).observe(this, Observer {
            it?.let { resource ->
                when (resource.status) {
                    SUCCESS -> {
                        recyclerView.visibility = View.VISIBLE
                        progressBar.visibility = View.GONE
                        resource.data?.let { data -> updateView(data.businesses) }
                    }
                    ERROR -> {
                        recyclerView.visibility = View.VISIBLE
                        progressBar.visibility = View.GONE
                     Toast.makeText(this, R.string.error_msg, Toast.LENGTH_LONG).show()
                    }
                    LOADING -> {
                        progressBar.visibility = View.VISIBLE
                        recyclerView.visibility = View.GONE
                    }
                }
            }
        })
    }

    private fun updateView(businesses : List<Business>){
        if(businesses?.size == 0)
            Toast.makeText(this, R.string.empty_data_msg, Toast.LENGTH_SHORT).show()

        adapter.apply {
            addData(businesses)
            notifyDataSetChanged()
        }
    }


    private fun checkPermission() {
        if (checkSelfPermissionCompat(Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED) {
            // Permission is already available
            layout.showSnackbar(R.string.location_permission_available, Snackbar.LENGTH_SHORT)
            getCurrentLocation()
        } else {
            // Permission is missing and must be requested.
            requestLocationPermission()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_REQUEST_LOCATION) {
            // Request for permission.
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission has been granted. Start searching location.
                layout.showSnackbar(R.string.location_permission_granted, Snackbar.LENGTH_SHORT)
                getCurrentLocation()
            } else {
                // Permission request was denied.
                layout.showSnackbar(R.string.location_access_required, Snackbar.LENGTH_INDEFINITE, R.string.ok) {

                    Intent(ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:${this!!.packageName}")).apply {
                        addCategory(Intent.CATEGORY_DEFAULT)
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(this)
                    }
                }
            }
        }
    }

    private fun requestLocationPermission() {
        // Permission has not been granted and must be requested.
        if (shouldShowRequestPermissionRationaleCompat(Manifest.permission.ACCESS_FINE_LOCATION)) {
            layout.showSnackbar(R.string.location_access_required,
                Snackbar.LENGTH_INDEFINITE, R.string.ok) {
                requestPermissionsCompat(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    PERMISSION_REQUEST_LOCATION)
            }

        } else {
            layout.showSnackbar(R.string.location_permission_not_available, Snackbar.LENGTH_SHORT)
            // Request the permission. The result will be received in onRequestPermissionResult().
            requestPermissionsCompat(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), PERMISSION_REQUEST_LOCATION)
        }
    }

    @SuppressLint("MissingPermission")
    private  fun getCurrentLocation(){
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location : Location? ->

                if(location != null) {
                    mLatitude = location?.latitude
                    mLongitude = location?.longitude
                }
            }
    }


    private  fun goToDetailPage(business : Business,  latitude : Double, longitude: Double){

        val nextScreenIntent = Intent(this, MapDetail::class.java).apply {
            putExtra("item", business)
            putExtra("latitude", latitude)
            putExtra("longitude", longitude)
        }
        startActivity(nextScreenIntent)
    }
}
