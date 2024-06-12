package com.openclassrooms.p8vitesse.domain.model

import com.openclassrooms.p8vitesse.data.entity.CandidateDto
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
    var about : String,
    var dateOfBirth : Date,
    var salaryExpectation : Int,
    var note : String,
    var topFavorite : Boolean

) {
    fun toDto(): CandidateDto {
        return CandidateDto(
            id = this.id?:0,
            lastName = this.lastName,
            firstName = this.firstName,
            phone = this.phone,
            email = this.email,
            about = this.about,
            dateOfBirth = this.dateOfBirth,
            salaryExpectation = this.salaryExpectation,
            note = this.note,
            topFavorite = this.topFavorite
        )
    }
}