package com.arjun.vaccinator.model


import com.google.gson.annotations.SerializedName

data class UserAgentResponse(
    @SerializedName("user-agent")
    val userAgent: String = ""
)