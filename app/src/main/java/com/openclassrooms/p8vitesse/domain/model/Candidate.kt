package com.openclassrooms.p8vitesse.domain.model

import android.net.Uri
import android.util.Log
import com.openclassrooms.p8vitesse.TAG_DEBUG
import com.openclassrooms.p8vitesse.data.entity.CandidateDto
import com.openclassrooms.p8vitesse.deleteFile
import com.openclassrooms.p8vitesse.moveFile
import java.io.File
import java.time.LocalDate
import java.time.Period
import java.time.ZoneId
import java.util.Date


/**
 * Data class of a Candidate (Model)
 */
data class Candidate (

    val id: Long? = null,
    var lastName : String,
    var firstName : String,
    var phone : String,
    var email : String,
    var dateOfBirth : Date,
    var salaryExpectation : Int,
    var note : String,
    var topFavorite : Boolean,

    // Pas présent en base de données
    // ce membre est utilisé pour amener l'image jusqu'au repository où la photo sera stockée avec un nom formaté
    var sPathTempSelectedPhoto : String

) {
    fun toDto(): CandidateDto {
        return CandidateDto(
            id = this.id?:0,
            lastName = this.lastName,
            firstName = this.firstName,
            phone = this.phone,
            email = this.email,
            dateOfBirth = this.dateOfBirth,
            salaryExpectation = this.salaryExpectation,
            note = this.note,
            topFavorite = this.topFavorite/*,
            photoFilePath = this.photoFilePath*/
        )
    }

    /**
     * Return the age of the candidate in year
     */
    fun nAge(): Int {

        // T034 - Display the age of the candidate

        val dateToday = Date() // Date actuelle

        // Convertir java.util.Date en LocalDate
        val localTodayDate: LocalDate = dateToday.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
        val localBirthday: LocalDate = this.dateOfBirth.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()

        // Calculer la différence en années

        val period = Period.between(localBirthday, localTodayDate)

        return if (period.years<0){
            0
        } else{
            period.years
        }



    }

    /**
     * Delete the photo reference by the candidate item
     */
    fun deletePhotoFile() {

        deleteFile(sGetPhotoPath())

    }

    /**
     * Transfère le fichier image vers un chemin formaté
     */
    // Prend en paramètre l'ID car je ne connais l'ID qu'après l'insert
    fun transferAndFormatPhotoFile(idP: Long = 0) {


        // Si un paramètre est passé (cas du insert)
        val sFormattedFile : String
        if (idP > 0){
            sFormattedFile = sGetPhotoPath(idP)
        }
        else{
            sFormattedFile = sGetPhotoPath()
        }

        moveFile(this.sPathTempSelectedPhoto, sFormattedFile)

        // Supprime le fichier temporaire
        deleteFile(this.sPathTempSelectedPhoto)

    }

    fun sGetPhotoPath(idP: Long = 0) : String {

        if (idP > 0){
            return File(fCurrentRep, getFileName(idP)).absolutePath
        }
        else{
            if (this.id==null){
                return ""
            }
            else{
                return File(fCurrentRep, getFileName(this.id)).absolutePath
            }
        }


    }

    // TODO : Mettre un booléen dans la base de données pour limiter les accès au système de fichier
    fun bPhotoExist() : Boolean {

        if (this.id==null){
            return false
        }
        else{
            val filePhoto = File(sGetPhotoPath())
            return filePhoto.exists()
        }


    }



    companion object {

        /**
         * Cette méthode est statique car je ne connais l'ID qu'après l'insert
         */
        fun getFileName(id: Long) : String {
            return "$id.jpg"
        }

        // Utilisation d'une globale
        // TODO : Voir pour faire plus propre
        var fCurrentRep : File? = null
        // DAns l'émulateur : /data/data/your.package.name/files/

    }





}