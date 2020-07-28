package com.anywhereapps.yelp.test.data.api

import com.anywhereapps.yelp.test.data.model.Establishment
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface ApiService {

    @GET("/v3/businesses/search")
    suspend fun getEstablishments(
        @Header("Authorization") token: String, @Query("term") term: String
        , @Query("latitude") latitude: String, @Query("longitude") longitude: String,
        @Query("radius") radius: String
    ): Establishment

}