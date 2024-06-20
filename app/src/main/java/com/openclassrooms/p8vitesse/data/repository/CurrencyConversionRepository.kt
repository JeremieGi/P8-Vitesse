package com.openclassrooms.p8vitesse.data.repository

import android.util.Log
import com.openclassrooms.p8vitesse.TAG_DEBUG
import com.openclassrooms.p8vitesse.data.network.ICurrencyAPI
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import java.util.Currency
import java.util.Locale


class CurrencyConversionRepository(
    private val dataService: ICurrencyAPI
) {


    fun listExchangeRates(sCurrencyCode : String) : Flow<ResultCustom<Map<String, Double>?>> = flow {

        emit(ResultCustom.Loading)

        // Par sécurité, on remet en minuscule
        val sCurrencyCodeLowerCase = sCurrencyCode.lowercase()


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

        Log.d(TAG_DEBUG, "Exception : ${error.message}")
    }


    /**
     * @param sCurrencyCodeFrom : eur / gbp
     * @param bOther : the finction return the other devise
     * @return : bOther=true => gbp if param sCodeFrom = eur, eur if param sCodeFrom = gbp
     *             bOther=false => gbp if param sCodeFrom = gbp, eur if param sCodeFrom = eur, gbp if param sCodeFrom = usd
     */
    fun getCurrencyWithCode(sCurrencyCodeFrom : String, bOther : Boolean): Currency {

        // Je suis obligé d'écrire cette fonction pour gérer le cas EUR (sinon Livre)
        // Aux USA, cette application affichera des livres

        val cResult : Currency

        when (sCurrencyCodeFrom.lowercase()){

            ICurrencyAPI.CURRENCY_CODE_EURO -> {
                if (bOther){
                    cResult = Currency.getInstance(Locale.UK)
                }
                else{
                    cResult = Currency.getInstance(Locale.FRANCE)
                }

            }

            else -> {
                if (bOther){
                    cResult = Currency.getInstance(Locale.FRANCE)
                }
                else{
                    cResult = Currency.getInstance(Locale.UK)
                }

            }

        }

        return cResult
    }



}