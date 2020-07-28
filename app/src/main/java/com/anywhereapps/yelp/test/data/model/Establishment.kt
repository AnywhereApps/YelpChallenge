package com.anywhereapps.yelp.test.data.model

data class Establishment(
    val businesses: List<Business>,
    val region: Region,
    val total: Int
)