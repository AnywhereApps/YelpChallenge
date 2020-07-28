package com.anywhereapps.yelp.test.data.api

class ApiHelper(private val apiService: ApiService) {

    suspend fun getEstablishments(token: String, term : String, latitude : String, longitude : String, radius : String) = apiService.getEstablishments(token, term, latitude, longitude, radius)
}