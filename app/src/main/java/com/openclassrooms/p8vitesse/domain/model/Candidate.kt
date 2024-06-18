package com.openclassrooms.p8vitesse.domain.model

import com.openclassrooms.p8vitesse.data.entity.CandidateDto
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
    var photoFilePath : String

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
            photoFilePath = this.photoFilePath
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
}