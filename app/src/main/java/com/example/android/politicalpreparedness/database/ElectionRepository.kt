package com.example.android.politicalpreparedness.database

import androidx.lifecycle.LiveData
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Election
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ElectionRepository(private val database: ElectionDatabase) {
    /**
     * A playlist of elections that can be shown on the screen.
     */
    val allElection: LiveData<List<Election>> = database.electionDao.getAllElection()

    suspend fun refreshElections() {
        withContext(Dispatchers.IO) {
            try {
                val elections = CivicsApi.retrofitService.getElections()
                val result = elections.elections
                database.electionDao.insertAllElection(*result.toTypedArray())
            } catch (err: Exception) {
                err.printStackTrace()
            }
        }
    }
}
