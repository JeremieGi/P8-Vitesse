package com.openclassrooms.p8vitesse

import android.content.Context
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.openclassrooms.p8vitesse.data.network.ICurrencyAPI
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

const val TAG_DEBUG = "**DEBUG**"


// TODO : A discuter avec Denis, utilisation d'un fichier de script Kotlin

fun sLocalDateToString(dDate : Date): String {
    val locale = Locale.getDefault()
    val dateFormat = SimpleDateFormat.getDateInstance(SimpleDateFormat.SHORT, locale)
    return dateFormat.format(dDate)
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

/**
 * @param sCodeFrom : eur / gbp
 * @return : gbp if param sCodeFrom = eur, eur if param sCodeFrom = gbp
 */
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


fun saveImageToInternalStorage(context: Context, uri: Uri): String {

    val inputStream = context.contentResolver.openInputStream(uri)
    if (inputStream == null){
        // TODO : Voir quoi faire ici
        return ""
    }
    else{
        val fileName = "${System.currentTimeMillis()}.jpg"
        val file = File(context.filesDir, fileName)

        val outputStream = FileOutputStream(file)
        inputStream.copyTo(outputStream)
        inputStream.close()
        outputStream.close()

        return file.absolutePath
    }




}

// TODO : Voir avec Denis où mettre cette fonction qui est appelée depuis 2 ViewModel - Peut-être dans le use case update ?
fun loadImageWithGlide(context: Context, filePath: String, imageView: ImageView) {

    Glide.with(context)
        .load(File(filePath))
        .into(imageView)

}