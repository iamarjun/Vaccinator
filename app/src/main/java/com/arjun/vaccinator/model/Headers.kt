package com.arjun.vaccinator.model


import com.google.gson.annotations.SerializedName

data class Headers(
    @SerializedName("Accept")
    val accept: String = "",
    @SerializedName("Accept-Encoding")
    val acceptEncoding: String = "",
    @SerializedName("Accept-Language")
    val acceptLanguage: String = "",
    @SerializedName("Host")
    val host: String = "",
    @SerializedName("Upgrade-Insecure-Requests")
    val upgradeInsecureRequests: String = "",
    @SerializedName("User-Agent")
    val userAgent: String = "",
    @SerializedName("X-Amzn-Trace-Id")
    val xAmznTraceId: String = ""
)