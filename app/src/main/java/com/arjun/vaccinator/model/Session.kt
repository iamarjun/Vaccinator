package com.arjun.vaccinator.model


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Session(
    @SerializedName("available_capacity")
    val availableCapacity: Int = 0,
    @SerializedName("block_name")
    val blockName: String = "",
    @SerializedName("center_id")
    val centerId: Int = 0,
    @SerializedName("date")
    val date: String = "",
    @SerializedName("district_name")
    val districtName: String = "",
    @SerializedName("fee")
    val fee: String = "",
    @SerializedName("fee_type")
    val feeType: String = "",
    @SerializedName("from")
    val from: String = "",
    @SerializedName("lat")
    val lat: Int = 0,
    @SerializedName("long")
    val long: Int = 0,
    @SerializedName("min_age_limit")
    val minAgeLimit: Int = 0,
    @SerializedName("name")
    val name: String = "",
    @SerializedName("pincode")
    val pincode: Int = 0,
    @SerializedName("session_id")
    val sessionId: String = "",
    @SerializedName("slots")
    val slots: List<String> = listOf(),
    @SerializedName("state_name")
    val stateName: String = "",
    @SerializedName("to")
    val to: String = "",
    @SerializedName("vaccine")
    val vaccine: String = ""
) : Parcelable