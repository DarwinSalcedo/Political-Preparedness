package com.example.android.politicalpreparedness.election

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.database.ElectionRepository
import com.example.android.politicalpreparedness.network.models.Election
import kotlinx.coroutines.launch

class ElectionsViewModel(application: Application) : AndroidViewModel(application) {

    private val database = ElectionDatabase.getInstance(application)
    private val electionRepository = ElectionRepository(database)

    //Create live data val for upcoming elections
    val electionUpcoming: LiveData<List<Election>>
        get() = electionRepository.allElection

    // Create val and functions to populate live data for upcoming elections from the API and saved elections from local database
    init {
        refreshElection()
    }

     fun refreshElection(){
        viewModelScope.launch {
            electionRepository.refreshElections()
        }
    }
    // Create functions to navigate to saved or upcoming election voter info
    private val _navigateToDetailElection = MutableLiveData<Election>()
    val navigateToDetailElection: LiveData<Election>
        get() = _navigateToDetailElection

    fun onElectionClicked(value: Election) {
        _navigateToDetailElection.value = value
    }

    fun onElectionNavigated() {
        _navigateToDetailElection.value = null
    }

}