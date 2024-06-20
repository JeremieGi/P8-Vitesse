package com.openclassrooms.p8vitesse.ui.candidate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.p8vitesse.data.repository.ResultCustom
import com.openclassrooms.p8vitesse.domain.model.Candidate
import com.openclassrooms.p8vitesse.domain.usecase.CandidateUseCaseDelete
import com.openclassrooms.p8vitesse.domain.usecase.CandidateUseCaseLoad
import com.openclassrooms.p8vitesse.domain.usecase.CandidateUseCaseSetFavorite
import com.openclassrooms.p8vitesse.domain.usecase.ConversionUseCase
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
    private val getCandidateUseCaseSetFavorite : CandidateUseCaseSetFavorite
) : ViewModel(){


    // Communication avec le fragment via StateFlow
    private val _candidateStateFlow = MutableStateFlow<CandidateUIState?>(null)
    val candidateStateFlow: StateFlow<CandidateUIState?> = _candidateStateFlow.asStateFlow() // Exposé au fragment (read only)


    // Propriété pour faciliter a manipulation du candidat courant et l'avoir dans le ViewModel
    private var _currentCandidate : Candidate? = null
    val currentCandidate: Candidate? // En lecture seulement
        get() = _currentCandidate
   


    // Charge le candidat depuis la base de données à partir de son ID
    fun loadCandidate(sIDCandidate: String?) {

        val lID : Long = sIDCandidate?.toLong()?:0
        viewModelScope.launch {

            val resultCandidate = getCandidateUseCaseLoad.execute(lID)

            resultCandidate.fold(
                onSuccess = { candidate ->
                    // Keep the candidate in the View Model
                    _currentCandidate = candidate

                    _candidateStateFlow.value = CandidateUIState.Success(candidate)
                },
                onFailure = { exception ->
                    _candidateStateFlow.value = CandidateUIState.Error(exception)
                }
            )


        }

    }

    /**
     * Delete a candidate
     */
    fun delete() {

        if (_currentCandidate!=null){

            val lID : Long = _currentCandidate?.id?:0
            if (lID > 0){
                viewModelScope.launch {
                    try{
                        getCandidateUseCaseDelete.execute(_currentCandidate!!) // !! => car test non null précédent
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
        else{
            _candidateStateFlow.value = CandidateUIState.Error(Exception("Current candidate Null"))
        }




    }

    /**
     * Call a webservice to do the conversion
     */
    fun conversion(
        nSalary : Int,
        cCurrencyFrom : Currency,
        cCurrencyTo : Currency
    ) {

        getConversionUseCase.execute(cCurrencyFrom.currencyCode).onEach { resultAPI ->

            // En fonction du résultat de l'API
            when (resultAPI) {

                // Echec
                is ResultCustom.Failure ->
                    _candidateStateFlow.value = CandidateUIState.Error(Exception(resultAPI.errorMessage))

                // En chargement
                ResultCustom.Loading -> {
                    // Pas géré => Le libellé initial indique que le chargement est en cours
                    //_candidateStateFlow.value = CandidateState.
                }

                // Succès
                is ResultCustom.Success -> {

                    val mapResult = resultAPI.value
                    if (mapResult!=null){

                        // Récupère le taux de change
                        val rate = mapResult[cCurrencyTo.currencyCode.lowercase()]?:0.0

                        // Effectue la conversion
                        val conversion = nSalary * rate

                        // Formate le résultat
                        val sRound = String.format("%.2f", conversion)
                        val sFormattedResult = "$sRound ${cCurrencyTo.symbol}"

                        // L'envoi au fragment
                        _candidateStateFlow.value = CandidateUIState.Conversion(sFormattedResult)

                    }




                }


            }

        }.launchIn(viewModelScope)


    }



    fun setFavorite(bNewFavoriteStatut: Boolean) {

        viewModelScope.launch {
            try{

                // let = section critique
                // sinon : Smart cast to 'Candidate' is impossible, because 'currentCandidate' is a mutable property that could have been changed by this time
                _currentCandidate?.let { candidate ->

                    if (candidate.id==null){

                        _candidateStateFlow.value = CandidateUIState.Error(Exception("Current candidate ID is null"))

                    }
                    else{

                        getCandidateUseCaseSetFavorite.execute(candidate.id,bNewFavoriteStatut)

                        // MAj en mémoire
                        candidate.topFavorite = bNewFavoriteStatut

                        _candidateStateFlow.value = CandidateUIState.OperationFavoriteUpdated

                    }

                }



            }
            catch (e : Exception){
                _candidateStateFlow.value = CandidateUIState.Error(e)
            }

        }

    }



    fun getOtherCurrencyWithCode(sCurrencyCodeFrom: String, bOther : Boolean) : Currency {

        return getConversionUseCase.getCurrencyWithCode(sCurrencyCodeFrom,bOther)

    }


}

