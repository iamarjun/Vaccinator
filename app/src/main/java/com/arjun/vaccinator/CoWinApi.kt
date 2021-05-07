package com.arjun.vaccinator

import com.arjun.vaccinator.model.DistrictsResponse
import com.arjun.vaccinator.model.HeadersResponse
import com.arjun.vaccinator.model.SessionResponse
import com.arjun.vaccinator.model.StatesResponse
import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.Path
import retrofit2.http.Query

@JvmSuppressWildcards
interface CoWinApi {

    @GET("admin/location/states")
    suspend fun getState(
        @HeaderMap headers: Map<String, Any>
    ): StatesResponse

    @GET("appointment/sessions/public/findByPin")
    suspend fun getSlotsForDate(
        @Query("pincode") pincode: Int,
        @Query("date") date: String,
        @HeaderMap headers: Map<String, Any>
    ): SessionResponse

    @GET("admin/location/districts/{state_id}")
    suspend fun getAllDistrictOfTheState(
        @Path("state_id") stateId: Int,
        @HeaderMap headers: Map<String, Any>
    ): DistrictsResponse

    @GET("appointment/sessions/public/findByDistrict")
    suspend fun getSlotsByDistrict(
        @Query("district_id") districtId: Int,
        @Query("date") date: String,
        @HeaderMap headers: Map<String, Any>
    ): SessionResponse

    @GET("http://httpbin.org/headers")
    suspend fun getHeader(): HeadersResponse
}