package com.openclassrooms.p8vitesse.data.repository

import android.util.Log
import com.openclassrooms.p8vitesse.MainApplication
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

        // Appel à l'API
        val sCurrencyCodeLowerCase = sCurrencyCode.lowercase() // Pour faciliter le fonctionnement
        val result = dataService.getConversions(sCurrencyCodeLowerCase)
        // si la requête met du temps, pas grave, on est dans une coroutine, le thread principal n'est pas bloqué

        /*
        // Transformation du résultat en données du Model
        val model = result.body()?.toDomainModel() ?: throw Exception("Invalid data")
        */
        
        val listResult : Map<String, Double>?
        val listEUR = result.body()?.listEUR
        val listGBP = result.body()?.listGBP

        if (listEUR.isNullOrEmpty()){
            listResult = listGBP
        }
        else{
            listResult = listEUR
        }

        Log.d(TAG_DEBUG, "Reponse du WS : ${listResult?.size} devises")

        // Ajout au flow

        emit(ResultCustom.Success(listResult))



    }.catch { error ->
        emit(ResultCustom.Failure(error.message+" "+error.cause?.message))
        Log.d(TAG_DEBUG, "Excepion dans ")
    }

}