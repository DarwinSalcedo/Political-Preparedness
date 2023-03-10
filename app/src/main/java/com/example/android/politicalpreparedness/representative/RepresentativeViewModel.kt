package com.example.android.politicalpreparedness.representative

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.election.CONECTION
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.representative.model.Representative
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RepresentativeViewModel : ViewModel() {

    // Establish live data for representatives and address
    private val _representatives = MutableLiveData<List<Representative>>()
    val representatives: LiveData<List<Representative>>
        get() = _representatives

    private val _address = MutableLiveData<Address>()
    val address: LiveData<Address>
        get() = _address

    private val _status = MutableLiveData<CONECTION>()
    val status: LiveData<CONECTION>
        get() = _status


    /**
     *  The following code will prove helpful in constructing a representative from the API. This code combines the two nodes of the RepresentativeResponse into a single official :

    val (offices, officials) = getRepresentativesDeferred.await()
    _representatives.value = offices.flatMap { office -> office.getRepresentatives(officials) }

    Note: getRepresentatives in the above code represents the method used to fetch data from the API
    Note: _representatives in the above code represents the established mutable live data housing representatives
     */
    // Create function to fetch representatives from API from a provided address
    fun fetchRepresentatives() {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    val (offices, officials) = CivicsApi.retrofitService.getRepresentatives(_address.value!!.toFormattedString())
                    _representatives.postValue(offices.flatMap { office ->
                        office.getRepresentatives(officials)
                    })
                }
                _status.value = CONECTION.CONNECTED
            } catch (err: Exception) {
                _status.value = CONECTION.DISCONNECTED
                err.printStackTrace()
            }
        }
    }

    // Create function get address from geo location
    // Create function to get address from individual fields
    fun getAddress(address: Address) {
        _address.value = address
    }


}

enum class CONECTION { CONNECTED, DISCONNECTED }
