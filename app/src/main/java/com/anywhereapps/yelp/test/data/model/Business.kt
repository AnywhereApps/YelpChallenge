package com.anywhereapps.yelp.test.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Business(
    val alias: String? = "",
    val categories: List<Category>? ,
    val coordinates: Coordinates?,
    val display_phone: String? = "",
    val distance: Double?,
    val id: String? ="",
    val image_url: String? ="",
    val is_closed: Boolean,
    val location: Location?,
    val name: String? = "",
    val phone: String? = "",
    val price: String? = "",
    val rating: Double?,
    val review_count: Int?,
    val url: String? = ""
): Parcelable