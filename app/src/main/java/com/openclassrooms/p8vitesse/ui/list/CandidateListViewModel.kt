package com.openclassrooms.p8vitesse.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.p8vitesse.data.repository.ResultCustom
import com.openclassrooms.p8vitesse.domain.usecase.CandidateUseCaseList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CandidateListViewModel @Inject constructor(
    private val getCandidateUseCaseList : CandidateUseCaseList
 ) : ViewModel() {


    // Class for the communication between the ViewModel and the fragment
    private val _uiState = MutableStateFlow(CandidateListUIStates())
    val uiState: StateFlow<CandidateListUIStates> = _uiState.asStateFlow()


    // Pour savoir si on a filtre sur les favoris ou non
    var bFavoriteOnly : Boolean = false


    // Lance la requête qui liste les candidats
    fun initCandidates() {

        // nFavorite =>
        // T005 - All candidates tab
        // T006 - Favorite candidates tab


        viewModelScope.launch {
            getCandidateUseCaseList.execute("") // Pas de filtre à la création du fragment
        }

    }

    // Ecoute en permanence le flow du repository
    fun observeCandidates() {

        // Durée de vie du scope = durée de vie du viewModel
        viewModelScope.launch {

            // Collecte du Flow
            getCandidateUseCaseList.allCandidatesFlow.collect { resultDB ->

                // result of the DB
                when (resultDB) {

                    // Fail
                    is ResultCustom.Failure -> _uiState.update { currentState ->
                        currentState.copy(
                            isLoading = false,
                            errorMessage = resultDB.errorMessage.toString()
                        )
                    }
                    // Loading
                    is ResultCustom.Loading -> _uiState.update { currentState ->
                        currentState.copy(
                            isLoading = true,
                            errorMessage = ""
                        )
                    }
                    // Success
                    is ResultCustom.Success -> _uiState.update { currentState ->

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



}