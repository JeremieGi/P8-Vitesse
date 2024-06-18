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


fun saveImageToInternalStorage(context: Context, uri: Uri): String {

    val inputStream = context.contentResolver.openInputStream(uri)
    if (inputStream == null){
        throw Exception("null inputStream")
        //return ""
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

// TODO : Mettre dans un fichier à part + enlever paramètre Context qu'on peut retrouver à partir du imageView
fun loadImageWithGlide(context: Context, filePath: String, imageView: ImageView) {

    Glide.with(context)
        .load(File(filePath))
        .into(imageView)

}