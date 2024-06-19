package com.openclassrooms.p8vitesse.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.openclassrooms.p8vitesse.domain.model.Candidate
import java.util.Date

/**
 * Entity Candidate in the database
 * DTO = Data Transfer Object
 */
@Entity(
    tableName = "tblCandidate"
)
data class CandidateDto(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long = 0,

    @ColumnInfo(name = "lastName")
    var lastName: String,

    @ColumnInfo(name = "firstName")
    var firstName: String,

    @ColumnInfo(name = "phone")
    var phone: String,

    @ColumnInfo(name = "email")
    var email: String,

    @ColumnInfo(name = "dateOfBirth")
    var dateOfBirth: Date, // Utilisation d'un Converter car SQLLite ne gère pas nativement les dates et soit utiliser des long

    @ColumnInfo(name = "salaryExpectation")
    var salaryExpectation: Int,

    @ColumnInfo(name = "note")
    var note: String,

    @ColumnInfo(name = "topFavorite")
    var topFavorite: Boolean,

    // Je stocke l'image dans un répertoire interne de l'appli avec le nom ID.png
    // pas besoin de stocker cette information
    //@ColumnInfo(name = "photoFilePath")
    //val photoFilePath: String

) {
    /**
     * Tranform candidate DTO in Candidate model
     */
    fun toModelCandidate() : Candidate {

        return Candidate(
            id = this.id,
            lastName = this.lastName,
            firstName = this.firstName,
            phone = this.phone,
            email = this.email,
            dateOfBirth = this.dateOfBirth,
            salaryExpectation = this.salaryExpectation,
            note = this.note,
            topFavorite = this.topFavorite,
            sPathTempSelectedPhoto = ""
        )

    }


}
