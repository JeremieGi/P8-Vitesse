package com.openclassrooms.p8vitesse

import com.openclassrooms.p8vitesse.domain.model.Candidate
import org.junit.Test
import java.util.Calendar

class CandidateTest {

    @Test
    fun test_Age(){

        val currentDate = Calendar.getInstance()
        currentDate.set(1980, 0, 1)

        val c = Candidate(
            id = 1,
            lastName = "Test",
            firstName = "Jé",
            phone ="",
            email ="",
            dateOfBirth = currentDate.time,
            salaryExpectation = 0,
            note ="",
            topFavorite = true,
            sPathTempSelectedPhoto =""
        )

        // Comment écrire un test maintenable mais qui ne réécrive pas la même fonction
        // je fais une comparaison
        assert(c.nAge() >= 44)

    }

    @Test
    fun test_Age_Limit(){

        // In the future
        val currentDate = Calendar.getInstance()
        currentDate.set(9999, 0, 1)

        val c = Candidate(
            id = 1,
            lastName = "Test",
            firstName = "Jé",
            phone ="",
            email ="",
            dateOfBirth = currentDate.time,
            salaryExpectation = 0,
            note ="",
            topFavorite = true,
            sPathTempSelectedPhoto =""
        )

        // Age à 0
        val nAge = c.nAge()

        assert(nAge == 0)

    }

}