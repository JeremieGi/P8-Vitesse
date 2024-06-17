package com.openclassrooms.p8vitesse.ui.candidate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.p8vitesse.data.repository.ResultCustom
import com.openclassrooms.p8vitesse.domain.model.Candidate
import com.openclassrooms.p8vitesse.domain.usecase.CandidateUseCaseDelete
import com.openclassrooms.p8vitesse.domain.usecase.CandidateUseCaseLoad
import com.openclassrooms.p8vitesse.domain.usecase.CandidateUseCaseUpdate
import com.openclassrooms.p8vitesse.domain.usecase.ConversionUseCase
import com.openclassrooms.p8vitesse.getOtherCurrency
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.Currency
import javax.inject.Inject

@HiltViewModel
class CandidateDisplayViewModel @Inject constructor(
    private val getCandidateUseCaseLoad : CandidateUseCaseLoad,
    private val getCandidateUseCaseDelete : CandidateUseCaseDelete,
    private val getConversionUseCase : ConversionUseCase,
    private val getCandidateUseCaseUpdate : CandidateUseCaseUpdate
) : ViewModel(){


    // Communication avec le fragment via StateFlow
    private val _candidateStateFlow = MutableStateFlow<CandidateUIState?>(null)
    val candidateStateFlow: StateFlow<CandidateUIState?> = _candidateStateFlow.asStateFlow() // Exposé au fragment (read only)


    // TODO : Propriété pour faciliter a manipulation du candidat courant
    // et l'avoir dans le ViewModel
    private lateinit var currentCandidate : Candidate


    // Charge le candidat depuis la base de données
    fun loadCandidate(sIDCandidate: String?) {

        val lID : Long = sIDCandidate?.toLong()?:0
        viewModelScope.launch {

            val resultCandidate = getCandidateUseCaseLoad.execute(lID)

            resultCandidate.fold(
                onSuccess = { candidate ->
                    // Keep the candidate in the View Model
                    currentCandidate = candidate

                    _candidateStateFlow.value = CandidateUIState.Success(candidate)
                },
                onFailure = { exception ->
                    _candidateStateFlow.value = CandidateUIState.Error(exception)
                }
            )


        }

    }

    fun getCurrentCandidate() : Candidate {
        // TODO : A reprendre avec une variable private val
        return currentCandidate
    }

    fun delete() {

        val lID : Long = currentCandidate.id?:0
        if (lID > 0){
            viewModelScope.launch {
                try{
                    getCandidateUseCaseDelete.execute(lID)
                    _candidateStateFlow.value = CandidateUIState.OperationDeleteCompleted
                }
                catch (e : Exception){
                    _candidateStateFlow.value = CandidateUIState.Error(e)
                }

            }
        }
        else{
            _candidateStateFlow.value = CandidateUIState.Error(Exception("Invalid ID $lID"))
        }


    }

    fun conversion(
        sCurrencyCodeFrom : String,
        dOriginalValue : Double
    ) {

        getConversionUseCase.execute(sCurrencyCodeFrom).onEach { resultAPI ->

            // En fonction du résultat de l'API
            when (resultAPI) {

                // Echec
                is ResultCustom.Failure ->
                    _candidateStateFlow.value = CandidateUIState.Error(Exception(resultAPI.errorMessage))

                // En chargement
                ResultCustom.Loading -> {
                    // TODO : pas fait : A faire ou à nettoyer
                    //_candidateStateFlow.value = CandidateState.
                }

                // Succès
                is ResultCustom.Success -> {

                    val mapResult = resultAPI.value
                    if (mapResult!=null){

                        val deviseTo = getOtherCurrency(sCurrencyCodeFrom)

                        val rate = mapResult[deviseTo]?:0.0
                        val conversion = dOriginalValue * rate
                        val sRound = String.format("%.2f", conversion)

                        val sFormattedResult = "$sRound ${Currency.getInstance(deviseTo).symbol}"

                        _candidateStateFlow.value = CandidateUIState.Conversion(sFormattedResult)

                    }




                }


            }

        }.launchIn(viewModelScope)


    }

    fun setFavorite(bNewFavoriteStatut: Boolean) {

        viewModelScope.launch {
            try{

                if (currentCandidate.id==null){

                    _candidateStateFlow.value = CandidateUIState.Error(Exception("Current candidate ID is null"))

                }
                else{

                    val lID = currentCandidate.id ?: 0

                    getCandidateUseCaseUpdate.setFavorite(lID,bNewFavoriteStatut)

                    // MAj en mémoire
                    currentCandidate.topFavorite = bNewFavoriteStatut

                    _candidateStateFlow.value = CandidateUIState.OperationFavoriteUpdated

                }


            }
            catch (e : Exception){
                _candidateStateFlow.value = CandidateUIState.Error(e)
            }

        }

    }


}

