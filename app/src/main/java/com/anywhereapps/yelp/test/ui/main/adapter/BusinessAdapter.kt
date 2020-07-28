package com.anywhereapps.yelp.test.ui.main.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.anywhereapps.yelp.test.R
import com.anywhereapps.yelp.test.data.model.Business
import com.anywhereapps.yelp.test.ui.main.adapter.BusinessAdapter.BusinessViewHolder
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_layout.view.*

class BusinessAdapter(private val businesses: ArrayList<Business>) : RecyclerView.Adapter<BusinessViewHolder>() {

    class BusinessViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(business: Business) {
            itemView.apply {
                businessName.text = business.name
                locationText.text = business.location.address1 + ", " + business.location.city
                Glide.with(imageViewAvatar.context)
                    .load(business.image_url)
                    .into(imageViewAvatar)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BusinessViewHolder =
        BusinessViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false))

    override fun getItemCount(): Int = businesses.size

    override fun onBindViewHolder(holder: BusinessViewHolder, position: Int) {
        holder.bind(businesses[position])
    }

    fun addData(businesses: List<Business>) {
        this.businesses.apply {
            clear()
            addAll(businesses)
        }

    }
}