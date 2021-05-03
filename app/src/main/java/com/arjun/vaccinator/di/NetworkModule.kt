package com.arjun.vaccinator.di

import com.arjun.vaccinator.BuildConfig
import com.arjun.vaccinator.RestApi
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
    fun providesOkhttpClientBuilder(): OkHttpClient.Builder {
        return OkHttpClient().newBuilder()
    }

    @Provides
    fun provideRetrofitBuilder(okHttpClientBuilder: OkHttpClient.Builder): Retrofit {
        return Retrofit.Builder()
            .client(okHttpClientBuilder.build())
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }


    @Provides
    fun provideRestApi(retrofit: Retrofit): RestApi {
        return retrofit.create(RestApi::class.java)
    }
}