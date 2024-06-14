package com.openclassrooms.p8vitesse.data.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * {
 *     "date": "2024-06-13",
 *     "eur": {
 *         "$myro": 5.24686803,
 *         "$wen": 6645.78404473,
 *              ...
 *          "eur": 1,
 *          ...
 *         "gbp": 0.84502646,
 *          }
 * }
 */
@JsonClass(generateAdapter = true)
data class APIResponseCurrency (

    @Json(name = "eur")
    val listEUR : Map<String, Double>? = null, // = null = optionnelle

    @Json(name = "gbp")
    val listGBP : Map<String, Double>? = null

)



