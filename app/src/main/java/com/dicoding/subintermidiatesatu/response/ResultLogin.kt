package com.dicoding.subintermidiatesatu.response

import com.google.gson.annotations.SerializedName

data class Login(

    @field:SerializedName("name")
    val name: String,

    @field:SerializedName("userId")
    val userId: String,

    @field:SerializedName("token")
    val token: String
)