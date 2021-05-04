package com.arjun.vaccinator.model


import com.google.gson.annotations.SerializedName

data class DistrictsResponse(
    @SerializedName("districts")
    val districts: List<District> = listOf(),
    @SerializedName("ttl")
    val ttl: Int = 0
)