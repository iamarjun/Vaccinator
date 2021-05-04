package com.arjun.vaccinator.model


import com.google.gson.annotations.SerializedName

data class State(
    @SerializedName("state_id")
    val stateId: Int = 0,
    @SerializedName("state_name")
    val stateName: String = ""
)