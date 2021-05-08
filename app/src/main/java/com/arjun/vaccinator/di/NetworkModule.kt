package com.arjun.vaccinator.di

import com.arjun.vaccinator.BuildConfig
import com.arjun.vaccinator.CoWinApi
import com.arjun.vaccinator.HttpBinApi
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    fun provideGson() = GsonBuilder()
        .setLenient()
        .create()

    @Provides
    fun providesOkhttpClientBuilder(): OkHttpClient.Builder {
        return OkHttpClient().newBuilder()
    }

    @Provides
    fun provideRetrofitBuilder(okHttpClientBuilder: OkHttpClient.Builder, gson: Gson): Retrofit {
        return Retrofit.Builder()
            .client(okHttpClientBuilder.build())
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    fun provideCoWinApi(retrofit: Retrofit): CoWinApi {
        return retrofit.create(CoWinApi::class.java)
    }

    @Provides
    fun provideHttpBinApi(retrofit: Retrofit): HttpBinApi {
        return retrofit.create(HttpBinApi::class.java)
    }
}