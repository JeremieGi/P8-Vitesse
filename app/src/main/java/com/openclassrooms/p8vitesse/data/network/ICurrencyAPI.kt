package com.openclassrooms.p8vitesse.data.network

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ICurrencyAPI {

    // Ex d'appel : https://cdn.jsdelivr.net/npm/@fawazahmed0/currency-api@latest/v1/currencies/eur.json

    companion object {
        const val CURRENCY_CODE_EURO = "eur"
        const val CURRENCY_CODE_POUND = "gbp"
    }

    //T036 - Integrate currency conversion API

    @GET("currencies/{currency}.json") // EndPoint eur / gbp
    suspend fun getConversions(@Path("currency") currencyCode: String): Response<APIResponseCurrency>

}