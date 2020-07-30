package com.anywhereapps.yelp.test.ui.main.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.anywhereapps.yelp.test.R
import com.anywhereapps.yelp.test.data.model.Business
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.bottomsheet_layout.*

class BusinessFragment : BottomSheetDialogFragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.bottomsheet_layout, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
          val business = arguments?.getParcelable<Business>("item")

        title.text = business?.name
      //  location.text = business?.location?.address1 + ", " + business?.location?.city
        location.text = business?.location?.display_address?.joinToString(",")
        rating.text = getString(R.string.rating , business?.rating?.toString())
        review.text = getString(R.string.review_count , business?.review_count?.toString())
        val distanceMiles  = business?.distance?.div(1609);
        distance.text = getString(R.string.distance_km ,String.format("%.0f", business?.distance))
        phone.text = business?.display_phone
        Glide.with(this)
            .load(business?.image_url)
            .into(image)
    }
}