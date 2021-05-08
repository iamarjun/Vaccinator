package com.arjun.vaccinator

import com.arjun.vaccinator.model.HeadersResponse
import com.arjun.vaccinator.model.UserAgentResponse
import retrofit2.http.GET

interface HttpBinApi {
    @GET("headers")
    suspend fun getHeader(): HeadersResponse

    @GET("user-agent")
    suspend fun getUserAgent(): UserAgentResponse
}