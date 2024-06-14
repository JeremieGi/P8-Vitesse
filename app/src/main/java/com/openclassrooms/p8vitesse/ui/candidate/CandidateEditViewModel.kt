package com.openclassrooms.p8vitesse.ui.candidate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.p8vitesse.domain.model.Candidate
import com.openclassrooms.p8vitesse.domain.usecase.CandidateUseCaseAdd
import com.openclassrooms.p8vitesse.domain.usecase.CandidateUseCaseLoad
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CandidateEditViewModel @Inject constructor(
    private val getCandidateUseCaseLoad : CandidateUseCaseLoad,
    private val getCandidateUseCaseAdd : CandidateUseCaseAdd
) : ViewModel(){

    private var _lIDCandidate : Long? = null
    val lIDCandidate = _lIDCandidate

    // Communication avec le fragment via StateFlow
    private val _candidateStateFlow = MutableStateFlow<CandidateState?>(null)
    val candidateStateFlow: StateFlow<CandidateState?> = _candidateStateFlow.asStateFlow() // Exposé au fragment (read only)


    fun loadCandidate(sIDCandidate: String?) {

        // TODO : cette procédure est identique à celle de candidateDisplayViewModel : est-elle factorisable

        _lIDCandidate = sIDCandidate?.toLong()

        val lID : Long = sIDCandidate?.toLong()?:0
        viewModelScope.launch {

            val resultCandidate = getCandidateUseCaseLoad.execute(lID)

            resultCandidate.fold(
                onSuccess = { candidate ->
                    _candidateStateFlow.value = CandidateState.Success(candidate)
                },
                onFailure = { exception ->
                    _candidateStateFlow.value = CandidateState.Error(exception)
                }
            )


        }

    }

    /**
     * @return : Mode add ? (2 modes : Add and Edit)
     */
    fun bModeAdd(): Boolean {
        return (_lIDCandidate == null)
    }

    /**
     * Add a candate
     */
    fun add(candidateNewData: Candidate) {

        viewModelScope.launch {
            try{
                val lIDCreated = getCandidateUseCaseAdd.execute(candidateNewData)
                _candidateStateFlow.value = CandidateState.OperationAddCompleted
            }
            catch (e : Exception){
                _candidateStateFlow.value = CandidateState.Error(e)
            }

        }

    }


}