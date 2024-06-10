package com.openclassrooms.p8vitesse.ui

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
    //private val savedStateHandle: SavedStateHandle // TODO : Je n'y suis pas arrivé : Cela permet de passer des paramètres supplémentaires qui ne peuvent pas être injectés directement.
) : ViewModel() {

    companion object {
        const val FAVORITE_CANDIDATE = 1
        const val ALL_CANDIDATE = -1
    }

    private var nFavorite: Int = ALL_CANDIDATE

    /**
     * StateFlow est une classe du framework Kotlin Flow qui émet une séquence de valeurs et garantit qu'un observateur reçoit toujours la dernière valeur émise.
     * Dans cet exemple, on expose un  StateFlow  en lecture seule à partir du  MutableStateFlow  créé précédemment.
     */
    private val _uiState = MutableStateFlow(CandidateListUIStates())
    val uiState: StateFlow<CandidateListUIStates> = _uiState.asStateFlow()

    // Load candidates (the observer will be notified in the fragment)
    fun loadAllCandidates() {

        // Call use case instance
        getCandidateUseCaseList.execute(nFavorite).onEach { resultDB ->

            // En fonction du résultat de la base de données
            when (resultDB) {

                // Echec
                is ResultDatabase.Failure -> _uiState.update { currentState ->
                    currentState.copy(
                        isLoading = false,
                        errorMessage = resultDB.message.toString()
                    )
                }
                // En chargement
                ResultDatabase.Loading -> _uiState.update { currentState ->
                    currentState.copy(
                        isLoading = true,
                        errorMessage = ""
                    )
                }
                // Succès
                is ResultDatabase.Success -> _uiState.update { currentState ->

                    // Récupération de la liste de candidats
                    currentState.copy(
                        listCandidates = resultDB.value,
                        isLoading = false,
                        errorMessage = ""
                    )
                }


            }

        }.launchIn(viewModelScope)


    }

    fun setFavoriteMode(nFavorite: Int) {
        this.nFavorite = nFavorite
    }


}