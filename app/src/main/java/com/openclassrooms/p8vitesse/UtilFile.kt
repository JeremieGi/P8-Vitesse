package com.openclassrooms.p8vitesse

import android.content.Context
import android.net.Uri
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.file.Files
import java.nio.file.StandardCopyOption

/**
 * Supprime un fichier
 */
fun deleteFile(photoFilePath: String) {

    val file = File(photoFilePath)
    try {
        if (file.exists())
            file.delete()

    } catch (e: IOException) {
        Log.d(TAG_DEBUG,"deleteFile $photoFilePath - An error occurred: ${e.message}")
    }

}

/**
 * Déplace un fichier
 */
fun moveFile(sourcePath: String, destinationPath: String): Boolean {

    return try {
        val sourceFile = File(sourcePath)
        val destinationFile = File(destinationPath)

        // Crée le répertoire parent du fichier de destination s'il n'existe pas
        destinationFile.parentFile?.mkdirs()

        deleteFile(destinationFile.absolutePath)

        // Utilise la classe Files pour déplacer le fichier, en remplaçant le fichier cible s'il existe
        Files.move(sourceFile.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING)

        true // Le déplacement a réussi
    } catch (e: IOException) {
        e.printStackTrace()
        false // Il y a eu une erreur lors du déplacement
    }
}

/**
 * Suve l'URI dans un fichier temporaire et renvoie le chemin de ce fichier
 */
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
