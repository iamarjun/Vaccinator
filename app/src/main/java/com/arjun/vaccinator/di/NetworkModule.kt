package com.arjun.vaccinator.di

import com.arjun.vaccinator.BuildConfig
import com.arjun.vaccinator.CoWinApi
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
        return OkHttpClient().newBuilder().addInterceptor {
            val request = it.request().newBuilder().addHeader(
                "User-Agent",
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.93 Safari/537.36"
            ).build()
            it.proceed(request)
        }
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
    fun provideRestApi(retrofit: Retrofit): CoWinApi {
        return retrofit.create(CoWinApi::class.java)
    }
}