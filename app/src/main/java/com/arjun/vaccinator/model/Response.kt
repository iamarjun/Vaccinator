package com.arjun.vaccinator.model


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Response(
    @SerializedName("sessions")
    val sessions: List<Session> = listOf()
) : Parcelable