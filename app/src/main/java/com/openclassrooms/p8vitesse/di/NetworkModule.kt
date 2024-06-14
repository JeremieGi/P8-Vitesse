package com.openclassrooms.p8vitesse.di

import com.openclassrooms.p8vitesse.data.network.ICurrencyAPI
import com.openclassrooms.p8vitesse.data.repository.CurrencyConversionRepository
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Singleton
    @Provides
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://cdn.jsdelivr.net/npm/@fawazahmed0/currency-api@latest/v1/")
            .addConverterFactory(
                MoshiConverterFactory.create(
                    Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
                )
            )
            .client(provideOkHttpClient()) // Uses a separate function for OkHttpClient configuration
            .build()
    }


    // Provides a singleton instance of APIClient using Retrofit
    /**
     * Cette fonction fournit une instance unique de APIClient utilisant Retrofit. Elle dépend de l'instance de Retrofit fournie par provideRetrofit
     */
    @Singleton
    @Provides
    fun provideAPIClient(retrofit: Retrofit): ICurrencyAPI {
        return retrofit.create(ICurrencyAPI::class.java)
    }

    // Private function to configure OkHttpClient with an interceptor for logging
    /**
     * Cette fonction configure et fournit une instance d'OkHttpClient avec un intercepteur pour les logs. Elle est utilisée dans provideRetrofit.
     * L'utilisation de l'intercepteur de logging est utile pour le débogage et pour comprendre ce qui se passe sous le capot lors de la communication HTTP.
     */
    private fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder().apply {
            addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            // Les intercepteurs sont très utiles pour effectuer un traitement systématique sur chaque requête envoyée
            // .setLevel(HttpLoggingInterceptor.Level.BODY) configure le niveau de logging de l'intercepteur. Level.BODY signifie que le corps des requêtes et des réponses sera logué, en plus des en-têtes et des autres informations.
        }.build()
    }

    /**
     * Constructeur du repository
     */
    @Provides
    @Singleton
    fun provideCurrencyConversionRepository(dataClient: ICurrencyAPI): CurrencyConversionRepository {
        return CurrencyConversionRepository(dataClient)
    }

}