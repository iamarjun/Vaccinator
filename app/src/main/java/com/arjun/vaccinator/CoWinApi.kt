package com.arjun.vaccinator

import com.arjun.vaccinator.model.DistrictsResponse
import com.arjun.vaccinator.model.SessionResponse
import com.arjun.vaccinator.model.StatesResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CoWinApi {

    @GET("admin/location/states")
    suspend fun getState(): StatesResponse

    @GET("appointment/sessions/public/findByPin")
    suspend fun getSlotsForDate(
        @Query("pincode") pincode: Int,
        @Query("date") date: String,
    ): SessionResponse

    @GET("admin/location/districts/{state_id}")
    suspend fun getAllDistrictOfTheState(
        @Path("state_id") stateId: Int
    ): DistrictsResponse

    @GET("appointment/sessions/public/findByDistrict")
    suspend fun getSlotsByDistrict(
        @Query("district_id") districtId: Int,
        @Query("date") date: String,
    ): SessionResponse
}