package com.openclassrooms.p8vitesse.domain.model

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
            topFavorite = this.topFavorite,
            nouveauchamptestV2 = ""
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
        val sFormattedFile = if (idP > 0){
            sGetPhotoPath(idP)
        } else{
            sGetPhotoPath()
        }

        moveFile(this.sPathTempSelectedPhoto, sFormattedFile)

        // Supprime le fichier temporaire
        deleteFile(this.sPathTempSelectedPhoto)

    }

    /**
     * Retourne le fichier de la photo du candidat. Chaine vide si ce fichier n'existe pas.
     */
    fun sGetPhotoPath(idP: Long = 0) : String {


        var sPath = ""
        if (idP > 0){
            sPath = File(_fCurrentRep, idP.toString()).absolutePath
        }
        else{
            if (this.id != null){
                sPath = File(_fCurrentRep, this.id.toString()).absolutePath
            }
        }

        sPath += ".jpg"

        return sPath

    }

    /**
     * A photo exist for this candidate
     */
    fun bPhotoExist() : Boolean {

        var sPath = ""
        if (this.id != null){
            sPath = sGetPhotoPath(this.id)
        }

        if (sPath!=""){
            return File(sPath).exists()
        }

        return false

    }


    companion object {

        // Utilisation d'une globale pour obtenir le répertoire de base de l'application

        private var _fCurrentRep : File? = null
        var fCurrentRep: File?  = null
            set(value) {
                _fCurrentRep = value
                field = value
            }

        // DAns l'émulateur : /data/data/your.package.name/files/

    }





}