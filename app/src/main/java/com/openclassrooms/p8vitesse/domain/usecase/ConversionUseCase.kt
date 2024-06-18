package com.openclassrooms.p8vitesse.domain.usecase

import com.openclassrooms.p8vitesse.data.repository.CurrencyConversionRepository
import com.openclassrooms.p8vitesse.data.repository.ResultCustom
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class ConversionUseCase @Inject constructor(
    private val currencyRepository: CurrencyConversionRepository
) {

    fun execute(sCurrencyCode: String): Flow<ResultCustom<Map<String, Double>?>> {
        return currencyRepository.listExchangeRates(sCurrencyCode)
    }
}