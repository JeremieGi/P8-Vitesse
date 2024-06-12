package com.openclassrooms.p8vitesse.ui.candidate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.p8vitesse.domain.model.Candidate
import com.openclassrooms.p8vitesse.domain.usecase.CandidateUseCaseList
import com.openclassrooms.p8vitesse.domain.usecase.CandidateUseCaseLoad
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CandidateDisplayViewModel @Inject constructor(
    private val getCandidateUseCaseLoad : CandidateUseCaseLoad
) : ViewModel(){


    // Communication avec le fragment via StateFlow
    private val _candidateFlow = MutableStateFlow<Result<Candidate>?>(null)
    val candidateFlow: StateFlow<Result<Candidate>?> = _candidateFlow.asStateFlow() // Exposé au fragment (read only)


    fun loadCandidate(sIDCandidate: String?) {

        val lID : Long = sIDCandidate?.toLong()?:0
        viewModelScope.launch {

            val candidate = getCandidateUseCaseLoad.execute(lID)
            _candidateFlow.value = candidate

        }

    }


}

