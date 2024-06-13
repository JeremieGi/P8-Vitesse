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
    private val _candidateFlow = MutableStateFlow<Result<Candidate>?>(null)
    val candidateFlow: StateFlow<Result<Candidate>?> = _candidateFlow.asStateFlow() // Exposé au fragment (read only)


    fun loadCandidate(sIDCandidate: String?) {

        _lIDCandidate = sIDCandidate?.toLong()

        viewModelScope.launch {

            val lID = _lIDCandidate?:0
            val candidate = getCandidateUseCaseLoad.execute(lID)
            _candidateFlow.value = candidate

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
            getCandidateUseCaseAdd.execute(candidateNewData)
        }

    }


}