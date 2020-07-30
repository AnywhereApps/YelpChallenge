package com.anywhereapps.yelp.test.data.api

class ApiHelper(private val apiService: ApiService) {

    suspend fun getEstablishments(
        token: String,
        term: String,
        latitude: Double,
        longitude: Double,
        radius: String
    ) = apiService.getEstablishments(token, term, latitude, longitude, radius)
}