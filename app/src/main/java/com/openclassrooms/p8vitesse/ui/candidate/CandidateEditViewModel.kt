package com.openclassrooms.p8vitesse.ui.candidate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.p8vitesse.domain.model.Candidate
import com.openclassrooms.p8vitesse.domain.usecase.CandidateUseCaseAdd
import com.openclassrooms.p8vitesse.domain.usecase.CandidateUseCaseLoad
import com.openclassrooms.p8vitesse.domain.usecase.CandidateUseCaseUpdate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CandidateEditViewModel @Inject constructor(
    private val getCandidateUseCaseLoad : CandidateUseCaseLoad,
    private val getCandidateUseCaseAdd : CandidateUseCaseAdd,
    private val getCandidateUseCaseUpdate : CandidateUseCaseUpdate
) : ViewModel(){


    // ID courant du candidat
    private var _idCandidate : Long? = null
    val idCandidate: Long? // En lecture seulement
        get() = _idCandidate


    // Candidat courant
    private var _currentCandidate : Candidate? = null
    var currentCandidate : Candidate?
        get() = _currentCandidate
        set(value) {
            _currentCandidate = value
        }


    // Communication avec le fragment via StateFlow
    private val _candidateStateFlow = MutableStateFlow<CandidateUIState?>(null)
    val candidateStateFlow: StateFlow<CandidateUIState?> = _candidateStateFlow.asStateFlow() // Exposé au fragment (read only)


    // Chargement d'un candidat
    fun loadCandidate(sIDCandidate: String?) {

        _idCandidate = sIDCandidate?.toLong()

        val lID : Long = sIDCandidate?.toLong()?:0
        viewModelScope.launch {

            val resultCandidate = getCandidateUseCaseLoad.execute(lID)

            resultCandidate.fold(
                onSuccess = { candidate ->
                    _candidateStateFlow.value = CandidateUIState.Success(candidate)
                },
                onFailure = { exception ->
                    _candidateStateFlow.value = CandidateUIState.Error(exception)
                }
            )

        }

    }

    /**
     * @return : Mode add ? (2 modes : Add and Edit)
     */
    fun bModeAdd(): Boolean {
        return ( _idCandidate==null )
    }

    /**
     * Add a candidate
     */
    fun add(candidateNewData: Candidate) {

        viewModelScope.launch {
            try{
                getCandidateUseCaseAdd.execute(candidateNewData)
                _candidateStateFlow.value = CandidateUIState.OperationAddCompleted
            }
            catch (e : Exception){
                _candidateStateFlow.value = CandidateUIState.Error(e)
            }

        }

    }


    /**
     * Update a candidate
     */
    fun update(candidate: Candidate) {

        viewModelScope.launch {
            try{
                getCandidateUseCaseUpdate.execute(candidate)
                _candidateStateFlow.value = CandidateUIState.OperationUpdatedCompleted
            }
            catch (e : Exception){
                _candidateStateFlow.value = CandidateUIState.Error(e)
            }

        }

    }


}