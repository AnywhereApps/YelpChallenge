package com.anywhereapps.yelp.test.ui.main.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.anywhereapps.yelp.test.data.repository.MainRepository
import com.anywhereapps.yelp.test.utils.Resource
import kotlinx.coroutines.Dispatchers


class MainViewModel(private val mainRepository: MainRepository) : ViewModel() {

    fun getEstablishments(token: String, term : String, latitude : Double, longitude : Double, radius : String) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainRepository.getEstablishment(token, term, latitude, longitude, radius)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }
}