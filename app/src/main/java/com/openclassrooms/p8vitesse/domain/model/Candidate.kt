package com.openclassrooms.p8vitesse.domain.model

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

)