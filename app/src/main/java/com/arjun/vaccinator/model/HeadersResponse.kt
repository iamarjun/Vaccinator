package com.arjun.vaccinator.model


import com.google.gson.annotations.SerializedName

data class HeadersResponse(
    @SerializedName("headers")
    val headers: Headers = Headers()
)