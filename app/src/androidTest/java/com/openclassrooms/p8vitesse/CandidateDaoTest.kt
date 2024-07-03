package com.openclassrooms.p8vitesse

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.openclassrooms.p8vitesse.data.dao.AppDataBase
import com.openclassrooms.p8vitesse.data.dao.CandidateDao
import com.openclassrooms.p8vitesse.data.entity.CandidateDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.runBlocking
import org.junit.After

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Before
import java.util.Calendar
import java.util.concurrent.CountDownLatch

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
@SmallTest
class CandidateDaoTest {

    private lateinit var database: AppDataBase
    private lateinit var cutCandidateDao: CandidateDao
    @Before
    fun setupDatabase() {
        database = Room.inMemoryDatabaseBuilder( // Ne crée pas de base de données physique
            ApplicationProvider.getApplicationContext(),
            AppDataBase::class.java
        ).allowMainThreadQueries().build()

        cutCandidateDao = database.candidateDao()
    }

    /**
     * Insère un candidat dans la base de test
     */
    private suspend fun insertCandidateTest(sNameP : String, bTopFavorite : Boolean) : Long {

        val currentDate = Calendar.getInstance()
        currentDate.set(1980, 0, 1)

        val candidateTest = CandidateDto(
            lastName = sNameP,
            firstName = "",
            phone = "",
            email = "",
            dateOfBirth = currentDate.time,
            salaryExpectation = 3000,
            note = "",
            topFavorite = bTopFavorite,
            nouveauchamptestV2 = "")

        return cutCandidateDao.insertCandidate(candidateTest)

    }

    @Test
    fun test_insertCandidate() = runBlocking {

        val sLastName = "LastName"

        val lID = insertCandidateTest(sLastName,bTopFavorite = false)

        var resultList: List<CandidateDto> = listOf()

        val latch = CountDownLatch(1)
        val job = async(Dispatchers.IO) {

            cutCandidateDao.getCandidates(null,null).collect { it ->

                resultList = it
                latch.countDown()

            }

        }
        latch.await()
        job.cancelAndJoin()

        // On attend 1 valeur
        assertEquals(1, resultList.size)
        assertEquals(1, lID)

        // Vérification que la donnée est bien insérée
        if (resultList.size==1){
            assertEquals(sLastName, resultList[0].lastName)
        }


    }


    @Test
    fun test_delete() = runBlocking {

        val sLastName2 = "LastName2"

        val lID1 = insertCandidateTest("LastName1",bTopFavorite = false)
        val lID2 = insertCandidateTest(sLastName2,bTopFavorite = false)

        assertEquals(1, lID1)
        assertEquals(2, lID2)

        cutCandidateDao.deleteCandidateById(lID1)

        var resultList: List<CandidateDto> = listOf()

        val latch = CountDownLatch(1)
        val job = async(Dispatchers.IO) {

            cutCandidateDao.getCandidates(null,null).collect { it ->

                resultList = it
                latch.countDown()

            }
        }
        latch.await()
        job.cancelAndJoin()

        // On attend 1 valeur
        assertEquals(1, resultList.size)

        // Vérification que c'est bien le 1er utilisateur qui a été supprimé
        if (resultList.size==1){
            assertEquals(sLastName2,resultList[0].lastName)
        }

    }



    @Test
    fun test_update() = runBlocking {

        val lID1 = insertCandidateTest("LastName1",bTopFavorite = false)

        assertEquals(1,lID1)

        val currentDate = Calendar.getInstance()
        currentDate.set(1980, 0, 1)

        val updatedValue = "nameUpdated"
        val updatedCandidate = CandidateDto(
            id = lID1,
            lastName = updatedValue,
            firstName = "",
            phone = "",
            email = "",
            dateOfBirth = currentDate.time,
            salaryExpectation = 3000,
            note = "",
            topFavorite = true,
            nouveauchamptestV2 = "")

        val nNbUpdatedLines = cutCandidateDao.updateCandidate(updatedCandidate)

        var resultList: List<CandidateDto> = listOf()

        val latch = CountDownLatch(1)
        val job = async(Dispatchers.IO) {

            cutCandidateDao.getCandidates(null,null).collect { it ->

                resultList = it
                latch.countDown()

            }
        }
        latch.await()
        job.cancelAndJoin()

        // On attend 1 valeur
        assertEquals(1, resultList.size)
        // Une ligne mise à jour
        assertEquals(1, nNbUpdatedLines)

        // Vérification que la donnée est bien été mise à jour
        if (resultList.size==1){
            assertEquals(updatedValue,resultList[0].lastName)
            assertEquals(true,resultList[0].topFavorite)
        }

    }


    @Test
    fun test_update_Favorite() = runBlocking {

        val lID1 = insertCandidateTest("LastName1",bTopFavorite = false)

        cutCandidateDao.updateCandidateTopFavorite(lID1,bFavorite = true)

        var resultList: List<CandidateDto> = listOf()

        val latch = CountDownLatch(1)
        val job = async(Dispatchers.IO) {

            cutCandidateDao.getCandidates(null,null).collect { it ->

                resultList = it
                latch.countDown()

            }
        }
        latch.await()
        job.cancelAndJoin()

        // On attend 1 valeur
        assertEquals(1, resultList.size)

        // Vérification que le candidat est bien un favori
        if (resultList.size==1){
            assert(resultList[0].topFavorite)
        }

    }

    @After
    fun closeDatabase() {
        database.close()
    }
}