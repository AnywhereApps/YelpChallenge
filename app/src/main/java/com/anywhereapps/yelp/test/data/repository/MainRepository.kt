package com.anywhereapps.yelp.test.data.repository

import com.anywhereapps.yelp.test.data.api.ApiHelper

class MainRepository(private val apiHelper: ApiHelper) {

    suspend fun getEstablishment(
        token: String,
        term: String,
        latitude: Double,
        longitude: Double,
        radius: String
    ) = apiHelper.getEstablishments(token, term, latitude, longitude, radius)
}