package com.example.userblinkitclone.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Users(
    @SerialName("id")
    val uid: String? = null,
    @SerialName("phone")
    val userPhoneNumber: String? = null,
    @SerialName("address")
    val userAddress: String? = null,
    @SerialName("state")
    val userState: String? = null,
    @SerialName("district")
    val userDistrict: String? = null,
    @SerialName("pincode")
    val userPinCode: String? = null,
)
