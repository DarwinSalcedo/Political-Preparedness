package com.example.android.politicalpreparedness.election

import androidx.lifecycle.*
import com.example.android.politicalpreparedness.common.convertAddress
import com.example.android.politicalpreparedness.common.formatDivisionVoterInfo
import com.example.android.politicalpreparedness.database.ElectionRepository
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class VoterInfoViewModel(
    private val repository: ElectionRepository,
    private val election: Election
) : ViewModel() {

    // Add live data to hold voter info
    private val _voterInfo = MutableLiveData<VoterInfoResponse>()
    val voterInfo: LiveData<VoterInfoResponse>
        get() = _voterInfo


    private val _state = MutableLiveData<CONECTION>()
    val state: LiveData<CONECTION>
        get() = _state

    val showNoData: MutableLiveData<Boolean> = MutableLiveData()

    private val _stateFollowed = MutableLiveData<Boolean>(false)
    val stateFollowed: LiveData<Boolean>
        get() = _stateFollowed


    // Add var and methods to populate voter info
    init {
        viewModelScope.launch {
            serviceVoterInfo()
            _stateFollowed.value = repository.isFollowed(election)
        }
    }

    private suspend fun serviceVoterInfo() {
        withContext(Dispatchers.Main) {
            try {
                withContext(Dispatchers.IO) {
                    val strDivision = formatDivisionVoterInfo(election.division)
                    _voterInfo.postValue(CivicsApi.retrofitService.getVoterinfo(strDivision,
                        election.id))
                }
                _state.value = CONECTION.CONNECTED
                showNoData.value = false
            } catch (err: Exception) {
                _state.value = CONECTION.DISCONNECTED
                showNoData.value = true
                err.printStackTrace()
            }
        }
    }

    // Add var and methods to support loading URLs
    val urlIntent = MutableLiveData<String>()
    fun loadingURLs(uri: String?) {
        urlIntent.value = uri
    }

    fun uriFormatAddress(address: Address?): String {
        return convertAddress(address)
    }


    fun onFollowedClick() {
        viewModelScope.launch {
            if (_stateFollowed.value!!) {
                repository.delFollowed(election)
            } else {
                repository.addFollowed(election)
            }
            _stateFollowed.value = repository.isFollowed(election)
        }
    }
    /**
     * Hint: The saved state can be accomplished in multiple ways. It is directly related to how elections are saved/removed from the repository.
     */
}

enum class CONECTION { CONNECTED, DISCONNECTED }
enum class ISFOLLOWED {FOLLOWED, UNFOLLOWED}