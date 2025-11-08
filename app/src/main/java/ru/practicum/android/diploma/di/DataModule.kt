package ru.practicum.android.diploma.di

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import org.koin.core.module.Module
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.practicum.android.diploma.BuildConfig
import ru.practicum.android.diploma.data.network.AreasApiService
import ru.practicum.android.diploma.data.network.NetworkClient
import ru.practicum.android.diploma.data.network.RetrofitNetworkClient
import ru.practicum.android.diploma.data.network.VacancySearchApiService

private const val BASE_URL_API = "https://practicum-diploma-8bc38133faba.herokuapp.com"

val dataModule: Module = module {

    single {
        OkHttpClient.Builder()
            .addInterceptor(Interceptor { chain ->
                val newRequest = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer ${BuildConfig.API_ACCESS_TOKEN}")
                    .build()
                chain.proceed(newRequest)
            })
            .build()
    }

    single<VacancySearchApiService> {
        Retrofit.Builder()
            .client(get())
            .baseUrl(BASE_URL_API)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(VacancySearchApiService::class.java)
    }

    single<AreasApiService> {
        Retrofit.Builder()
            .client(get())
            .baseUrl(BASE_URL_API)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AreasApiService::class.java)
    }

    single<NetworkClient> {
        RetrofitNetworkClient(get(), get())
    }
}
