package com.anywhereapps.yelp.test.ui.main.view

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.anywhereapps.yelp.test.R.*
import com.anywhereapps.yelp.test.data.api.ApiHelper
import com.anywhereapps.yelp.test.data.api.RetrofitBuilder
import com.anywhereapps.yelp.test.data.model.Business
import com.anywhereapps.yelp.test.ui.base.ViewModelFactory
import com.anywhereapps.yelp.test.ui.main.adapter.BusinessAdapter
import com.anywhereapps.yelp.test.ui.main.viewmodel.MainViewModel
import com.anywhereapps.yelp.test.utils.Status.ERROR
import com.anywhereapps.yelp.test.utils.Status.LOADING
import com.anywhereapps.yelp.test.utils.Status.SUCCESS
import kotlinx.android.synthetic.main.activity_main.progressBar
import kotlinx.android.synthetic.main.activity_main.recyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel
    private lateinit var adapter: BusinessAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_main)
        setupViewModel()
        setupUI()

        getData(RetrofitBuilder.TOKEN, "", "43.67441", "-79.39672", "25")
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
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter =
            BusinessAdapter(
                arrayListOf()
            )
        recyclerView.addItemDecoration(
            DividerItemDecoration(
                recyclerView.context,
                (recyclerView.layoutManager as LinearLayoutManager).orientation
            )
        )
        recyclerView.adapter = adapter
    }


    private fun getData(token: String, term : String, latitude : String, longitude : String, radius : String){

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
                        Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
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
        adapter.apply {
            addData(businesses)
            notifyDataSetChanged()
        }
    }
}
