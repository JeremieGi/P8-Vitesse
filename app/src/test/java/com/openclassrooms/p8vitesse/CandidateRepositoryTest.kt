package com.openclassrooms.p8vitesse

import android.util.Log
import com.openclassrooms.p8vitesse.data.dao.CandidateDao
import com.openclassrooms.p8vitesse.data.entity.CandidateDto
import com.openclassrooms.p8vitesse.data.repository.CandidateRepository
import com.openclassrooms.p8vitesse.data.repository.ResultCustom
import com.openclassrooms.p8vitesse.domain.model.Candidate
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import org.junit.Test

import org.junit.Before
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import java.util.Calendar

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@OptIn(ExperimentalCoroutinesApi::class) // pour utilisation de advanceUntilIdle()
class CandidateRepositoryTest {

    // TODO prio : 6 TU => 2 par repository (Room / Retrofit) + 2 sur l'age dans Candidate : quoi d'autres de pertinent ?

    private lateinit var cutCandidateRepository : CandidateRepository //Class Under Test
    private lateinit var mockCandidateDao: CandidateDao

    /**
     * Create mock object
     */
    @Before
    fun createRepositoryWithMockedDao() {

        mockCandidateDao = mockk()
        cutCandidateRepository = CandidateRepository(mockCandidateDao)

        // Mock the Log class
        // Log utilise le framework Android et ne peut pas être appelé dans un test unitaire
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0

    }


    /**
     * return list to simulate the result of Room
     */
    private fun getMockList() : List<CandidateDto> {

        val listReturn : MutableList<CandidateDto> = mutableListOf()

        val currentDate = Calendar.getInstance()
        currentDate.set(1980, 0, 1)

        // Create 9 candidates
        for (i in 1 until 9) {

            // A favorite for all 3 candidates
            val bFavorite = (i%3) == 0

            val candidateTest = CandidateDto(
                lastName = "LastName$i",
                firstName = "FirstName$i",
                phone = "06.12.34.35.3$i",
                email = "firstname$i.lastname$i@free.fr",
                dateOfBirth = currentDate.time,
                salaryExpectation = 3000+(i*100),
                note = "note$i \nDuplexque isdem diebus acciderat malum, quod et Theophilum insontem atrox interceperat casus",
                topFavorite = bFavorite,
                photoFilePath = ""
            )

            // Insert in the DB
            listReturn.add(candidateTest)

            // Add one year
            currentDate.add(Calendar.DAY_OF_YEAR, 1)

        }

        return listReturn

    }


    /**
     * Convertit une List de CandidateDto en Candidate
     */
    private fun convertToModelList(mockedList : List<CandidateDto>) : List<Candidate> {

        val listModel = mockedList.map {
            it.toModelCandidate()
        }

        return listModel

    }


    /**
     * Cas basique d'une liste simple
     */
    @Test
    fun test_getListAllCandidates_BasicCase() = runTest {

        // definition du mock
        val listCandidates = getMockList()
        coEvery {
            mockCandidateDao.getCandidates(any(),any())
        } returns flowOf(listCandidates)


        // Créer le collecteur du flow du repository
        val resultList = mutableListOf<ResultCustom<List<Candidate>>>()
        val job = launch {
            cutCandidateRepository.allCandidatesFlow.collect { result ->
                resultList.add(result)
            }
        }

        //when => Test réel de la fonction
        run {
            cutCandidateRepository.getListAllCandidates("")
        }
        // Note : J'ai du mettre org.gradle.jvmargs=-noverify  dans gradle.properties pour que les logs sont ignorés par les tests

        // coVerify : s'assure que la fonction  du mock  a été appelée
        coVerify {
            mockCandidateDao.getCandidates(any(),any())
        }

        // Attend que toutes les couroutines en attente s'executent
        advanceUntilIdle()

        // On attend 2 valeurs dans le flow du repository
        assertEquals(2, resultList.size)

        if (resultList.isNotEmpty()) {

            // Première valeur => Loading
            assertEquals(ResultCustom.Loading, resultList[0])

            // Deuxième valeur => La liste de candidat
            val expectedCandidateList = convertToModelList(listCandidates)
            val expectedResult = ResultCustom.Success(expectedCandidateList)
            assertEquals(expectedResult, resultList[1])

        }

        // Cancel the collection job
        job.cancel()


    }

    /**
     * Room retourne une exception
     */
    @Test
    fun test_getListAllCandidates_Exception() = runTest {

        // definition du mock => raise Exception
        val sExpectedException = "Test exception"
        coEvery {
            mockCandidateDao.getCandidates(any(),any())
        } throws Exception(sExpectedException)


        // Créer le collecteur du flow du repository
        val resultList = mutableListOf<ResultCustom<List<Candidate>>>()
        val job = launch {
            cutCandidateRepository.allCandidatesFlow.collect { result ->
                resultList.add(result)
            }
        }

        //when => Test réel de la fonction
        run {
            cutCandidateRepository.getListAllCandidates("ABC")
        }
        // Note : J'ai du mettre org.gradle.jvmargs=-noverify  dans gradle.properties pour que les logs sont ignorés par les tests

        // coVerify : s'assure que la fonction  du mock  a été appelée
        coVerify {
            mockCandidateDao.getCandidates(any(),any())
        }

        // Attend que toutes les couroutines en attente s'executent
        advanceUntilIdle()

        // On attend 2 valeurs dans le flow du repository
        assertEquals(2, resultList.size)

        if (resultList.isNotEmpty()) {

            // Première valeur => Loading
            assertEquals(ResultCustom.Loading, resultList[0])

            // Deuxième valeur => Erreur
            val expectedResult = ResultCustom.Failure(sExpectedException)
            assertEquals(expectedResult, resultList[1])

        }

        // Cancel the collection job
        job.cancel()


    }


}