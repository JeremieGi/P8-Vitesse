package com.openclassrooms.p8vitesse.ui.candidate

import androidx.lifecycle.ViewModel
import com.openclassrooms.p8vitesse.domain.usecase.CandidateUseCaseAdd
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CandidateEditViewModel @Inject constructor(
    private val getCandidateUseCaseAdd : CandidateUseCaseAdd
) : ViewModel(){



}