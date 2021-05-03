package com.arjun.vaccinator

import com.arjun.vaccinator.model.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface RestApi {

    @GET("v2/appointment/sessions/public/findByPin")
    suspend fun getSlotsForDate(
        @Query("pincode") pincode: Int,
        @Query("date") date: String,
    ): Response
}