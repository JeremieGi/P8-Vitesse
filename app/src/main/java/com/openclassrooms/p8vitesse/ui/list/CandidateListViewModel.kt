package com.openclassrooms.p8vitesse.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.p8vitesse.data.repository.ResultDatabase
import com.openclassrooms.p8vitesse.domain.usecase.CandidateUseCaseList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class CandidateListViewModel @Inject constructor(
    private val getCandidateUseCaseList : CandidateUseCaseList
 ) : ViewModel() {


    var bFavorite: Boolean? = null

    var sFilter: String? = null
        // Accesseur custom
        set(value) {
            // si le filtre est pas null ou vide => Null
            field = if (!value.isNullOrEmpty()) {
                value
            } else {
                null
            }
        }

    // Class for the communication between the ViewModel and the fragment
    private val _uiState = MutableStateFlow(CandidateListUIStates())
    val uiState: StateFlow<CandidateListUIStates> = _uiState.asStateFlow()

    // Load candidates (the observer of UI State will be notified in the fragment)
    fun loadCandidates() {

        // nFavorite =>
        // T005 - All candidates tab
        // T006 - Favorite candidates tab

        // Call use case instance
        getCandidateUseCaseList.execute(bFavorite,sFilter).onEach { resultDB ->

            // result of the DB
            when (resultDB) {

                // Fail
                is ResultDatabase.Failure -> _uiState.update { currentState ->
                    currentState.copy(
                        isLoading = false,
                        errorMessage = resultDB.message.toString()
                    )
                }
                // Loading
                is ResultDatabase.Loading -> _uiState.update { currentState ->
                    currentState.copy(
                        isLoading = true,
                        errorMessage = ""
                    )
                }
                // Success
                is ResultDatabase.Success -> _uiState.update { currentState ->

                    // transfert de la liste de candidats
                    currentState.copy(
                        listCandidates = resultDB.value,
                        isLoading = false,
                        errorMessage = ""
                    )
                }


            }

        }.launchIn(viewModelScope)


    }



}