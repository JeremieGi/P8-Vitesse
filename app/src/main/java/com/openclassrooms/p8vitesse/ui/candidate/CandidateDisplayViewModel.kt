package com.openclassrooms.p8vitesse.ui.candidate

import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.p8vitesse.domain.model.Candidate
import com.openclassrooms.p8vitesse.domain.usecase.CandidateUseCaseDelete
import com.openclassrooms.p8vitesse.domain.usecase.CandidateUseCaseLoad
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CandidateDisplayViewModel @Inject constructor(
    private val getCandidateUseCaseLoad : CandidateUseCaseLoad,
    private val getCandidateUseCaseDelete : CandidateUseCaseDelete
) : ViewModel(){


    // Communication avec le fragment via StateFlow
    private val _candidateStateFlow = MutableStateFlow<CandidateState?>(null)
    val candidateStateFlow: StateFlow<CandidateState?> = _candidateStateFlow.asStateFlow() // Exposé au fragment (read only)


    // TODO : Propriété pour faciliter a manipulation du candidat courant
    // et l'avoir dans le ViewModel
    private lateinit var currentCandidate : Candidate


    fun loadCandidate(sIDCandidate: String?) {

        val lID : Long = sIDCandidate?.toLong()?:0
        viewModelScope.launch {

            val resultCandidate = getCandidateUseCaseLoad.execute(lID)

            resultCandidate.fold(
                onSuccess = { candidate ->
                    // Keep the candidate in the View Model
                    currentCandidate = candidate

                    _candidateStateFlow.value = CandidateState.Success(candidate)
                },
                onFailure = { exception ->
                    _candidateStateFlow.value = CandidateState.Error(exception)
                }
            )


        }

    }

    fun getCurrentCandidate() : Candidate {
        return currentCandidate
    }

    fun delete() {

        val lID : Long = currentCandidate.id?:0
        if (lID > 0){
            viewModelScope.launch {
                try{
                    getCandidateUseCaseDelete.execute(lID)
                    _candidateStateFlow.value = CandidateState.OperationDeleteCompleted
                }
                catch (e : Exception){
                    _candidateStateFlow.value = CandidateState.Error(e)
                }

            }
        }
        else{
            _candidateStateFlow.value = CandidateState.Error(Exception("Invalid ID $lID"))
        }


    }


}

