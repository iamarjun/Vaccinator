package com.arjun.vaccinator.model


import com.google.gson.annotations.SerializedName

data class District(
    @SerializedName("district_id")
    val districtId: Int = 0,
    @SerializedName("district_name")
    val districtName: String = ""
)