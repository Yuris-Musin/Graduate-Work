package ru.musindev.graduate_work.di.modules

import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.musindev.graduate_work.data.api.YandexRaspApi
import javax.inject.Singleton

@Module
object NetworkModule {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.rasp.yandex.net/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideYandexRaspApi(retrofit: Retrofit): YandexRaspApi {
        return retrofit.create(YandexRaspApi::class.java)
    }
}