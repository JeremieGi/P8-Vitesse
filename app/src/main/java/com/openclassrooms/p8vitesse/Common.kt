package com.openclassrooms.p8vitesse

import com.openclassrooms.p8vitesse.data.network.ICurrencyAPI
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

const val TAG_DEBUG = "**DEBUG**"


// TODO : A discuter avec Denis, utilisation d'un fichier de script Kotlin

fun sLocalDateToString(dDate : Date): String {
    val locale = Locale.getDefault()
    val dateFormat = SimpleDateFormat.getDateInstance(SimpleDateFormat.SHORT, locale)
    val formattedDate = dateFormat.format(dDate)
    return formattedDate
}


fun dStringToLocalDate(sDate : String) : Date? {
    val locale = Locale.getDefault()
    val dateFormat = SimpleDateFormat.getDateInstance(SimpleDateFormat.SHORT, locale)
    return try {
        dateFormat.parse(sDate)
    } catch (e: Exception) {
        null
    }
}

// Met la première lettre en majuscule
fun String.capitalized(): String {
    return this.replaceFirstChar {
        if (it.isLowerCase())
            it.uppercase()
        else it.toString()
    }
}

fun getOtherCurrency(sCodeFrom : String): String {

    var sResult : String = ""


    when (sCodeFrom.lowercase()){

        ICurrencyAPI.CURRENCY_CODE_POUND -> {
            sResult = ICurrencyAPI.CURRENCY_CODE_EURO
        }

        ICurrencyAPI.CURRENCY_CODE_EURO -> {
            sResult = ICurrencyAPI.CURRENCY_CODE_POUND
        }

    }

    // TODO : Voir ici comment gérer l'erreur

    return sResult
}