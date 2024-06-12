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
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CandidateListViewModel @Inject constructor(
    private val getCandidateUseCaseList : CandidateUseCaseList
 ) : ViewModel() {

    var bFavoriteOnly : Boolean = false

    /*
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
     */

    // Class for the communication between the ViewModel and the fragment
    private val _uiState = MutableStateFlow(CandidateListUIStates())
    val uiState: StateFlow<CandidateListUIStates> = _uiState.asStateFlow()

    init {

        // Ecoute en permanence le flow du repository

        // (the observer of UI State will be notified in the fragment)
        viewModelScope.launch {

            getCandidateUseCaseList.allCandidatesFlow.collect { resultDB ->

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

                        // Récupération de la liste des candidats
                        var listCandidate = resultDB.value

                        // Si le filtre des favoris doit-être appliqué
                        if (bFavoriteOnly){
                            listCandidate = listCandidate.filter {
                                it.topFavorite
                            }
                        }

                        // transfert de la liste de candidats
                        currentState.copy(
                            listCandidates = listCandidate,
                            isLoading = false,
                            errorMessage = ""
                        )
                    }


                }

            }
        }

    }

    // searchCandidates
    fun initCandidates() {

        // nFavorite =>
        // T005 - All candidates tab
        // T006 - Favorite candidates tab


        viewModelScope.launch {
            getCandidateUseCaseList.execute("") // Pas de filtre à la création du fragment
        }

    }



}