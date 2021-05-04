package com.arjun.vaccinator.model


import com.google.gson.annotations.SerializedName

data class StatesResponse(
    @SerializedName("states")
    val states: List<State> = listOf(),
    @SerializedName("ttl")
    val ttl: Int = 0
)