package com.anywhereapps.yelp.test.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Category(
    val alias: String? = "",
    val title: String? = ""
): Parcelable