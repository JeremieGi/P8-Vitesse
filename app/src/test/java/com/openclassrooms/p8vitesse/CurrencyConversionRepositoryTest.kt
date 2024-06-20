package com.openclassrooms.p8vitesse

import android.util.Log
import com.openclassrooms.p8vitesse.data.network.APIResponseCurrency
import com.openclassrooms.p8vitesse.data.network.ICurrencyAPI
import com.openclassrooms.p8vitesse.data.repository.CurrencyConversionRepository
import com.openclassrooms.p8vitesse.data.repository.ResultCustom
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class CurrencyConversionRepositoryTest {

    private lateinit var cutCurrencyConversionRepository : CurrencyConversionRepository //Class Under Test
    private lateinit var mockAPI : ICurrencyAPI

    /**
     * Create mock object
     */
    @Before
    fun createRepositoryWithMockedDao() {

        mockAPI = mockk()
        cutCurrencyConversionRepository = CurrencyConversionRepository(mockAPI)

        // Mock the Log class
        // Log utilise le framework Android et ne peut pas être appelé dans un test unitaire
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0

    }

    /**
     * Test a basic conversion
     */
    @Test
    fun testBasicCaseEURtoGBP() = runTest {

        // MOCK
        val mapMockedResult = mutableMapOf<String, Double>()
        mapMockedResult["GBP"] = 0.8
        mapMockedResult["DOL"] = 0.9

        val mockResponse = APIResponseCurrency(
            listEUR = null,
            listGBP = mapMockedResult)

        coEvery {
            mockAPI.getConversions(any())
        } returns Response.success(mockResponse)


        //when => Test réel de la fonction
        val listResult = run {
            cutCurrencyConversionRepository.listExchangeRates(ICurrencyAPI.CURRENCY_CODE_EURO).toList()
        }

        coVerify {
            mockAPI.getConversions(any())
        }

        // On attend 2 valeurs dans le flow
        assertEquals(2, listResult.size)

        // Première valeur => Loading
        assertEquals(ResultCustom.Loading, listResult[0])

        // Deuxième valeur => La réponse avec succès
        val res = ResultCustom.Success(mapMockedResult)
        assertEquals(res, listResult[1])



    }

    /**
     * Simule une erreur 404
     */
    @Test
    fun testNetworkProblem() = runTest {

        val errorResponseBody = "Error 404 message".toResponseBody("text/plain".toMediaType())

        // Le mock renverra une erreur 404
        coEvery {
            mockAPI.getConversions(any())
        } returns Response.error<APIResponseCurrency>(404, errorResponseBody)

        //when => Test réel de la fonction
        val listResult = run {
            cutCurrencyConversionRepository.listExchangeRates(ICurrencyAPI.CURRENCY_CODE_EURO).toList()
        }

        coVerify {
            mockAPI.getConversions(any())
        }

        // On attend 2 valeurs dans le flow
        assertEquals(2, listResult.size)
        // Première valeur => Loading
        assertEquals(ResultCustom.Loading, listResult[0])
        // Deuxième valeur => Erreur
        assert(listResult[1] is ResultCustom.Failure)

    }

}