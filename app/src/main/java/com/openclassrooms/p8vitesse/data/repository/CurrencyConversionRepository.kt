package com.openclassrooms.p8vitesse.data.repository

import android.util.Log
import com.openclassrooms.p8vitesse.TAG_DEBUG
import com.openclassrooms.p8vitesse.data.network.ICurrencyAPI
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow


class CurrencyConversionRepository(
    private val dataService: ICurrencyAPI
) {


    fun listExchangeRates(sCurrencyCode : String) : Flow<ResultCustom<Map<String, Double>?>> = flow {

        emit(ResultCustom.Loading)

        // Pour faciliter les appels
        var sCurrencyCodeLowerCase = sCurrencyCode.lowercase()
        if (sCurrencyCodeLowerCase != ICurrencyAPI.CURRENCY_CODE_EURO){
            // Pas Euro = Pound (par exemple, l'émulateur Android utilise des dollars'
            sCurrencyCodeLowerCase = ICurrencyAPI.CURRENCY_CODE_POUND
        }

        // Appel à l'API

        val responseRetrofit = dataService.getConversions(sCurrencyCodeLowerCase)
        // si la requête met du temps, pas grave, on est dans une coroutine, le thread principal n'est pas bloqué

        if (responseRetrofit.isSuccessful){

            val listResult : Map<String, Double>?
            val listEUR = responseRetrofit.body()?.listEUR
            val listGBP = responseRetrofit.body()?.listGBP

            // Dans le json de retour, soit la liste s'appelle EUR, soit GBP
            listResult = if (listEUR.isNullOrEmpty()){
                listGBP
            } else{
                listEUR
            }

            Log.d(TAG_DEBUG, "Reponse du WS : ${listResult?.size} devises")

            // Ajout au flow
            emit(ResultCustom.Success(listResult))

        }
        else{

            emit(ResultCustom.Failure("Error code ${responseRetrofit.code()}"))

        }





    }.catch { error ->
        emit(ResultCustom.Failure(error.message+" "+error.cause?.message))
        Log.d(TAG_DEBUG, "Excepion dans ")
    }


    /**
     * @param sCodeFrom : eur / gbp
     * @return : gbp if param sCodeFrom = eur, eur if param sCodeFrom = gbp
     */
    fun getOtherCurrency(sCodeFrom : String): String {

        var sResult = when (sCodeFrom.lowercase()){

            ICurrencyAPI.CURRENCY_CODE_EURO -> {
                ICurrencyAPI.CURRENCY_CODE_POUND
            }

            else -> {
                ICurrencyAPI.CURRENCY_CODE_EURO
            }

        }

        return sResult
    }

}