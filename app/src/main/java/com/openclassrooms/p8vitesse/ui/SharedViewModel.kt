package com.openclassrooms.p8vitesse.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * ViewModel partagé entre MainFragment et CandidateListFragment pour connaître le filtre à appliquer
 */
class SharedViewModel : ViewModel() {

    private val _searchQuery = MutableLiveData<String>()
    val searchQuery: LiveData<String> get() = _searchQuery

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

}