package com.dicoding.subintermidiatesatu.response


import com.google.gson.annotations.SerializedName

data class UserLoginResponse(

    @field:SerializedName("loginResult")
    val loginResult: Login,

    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String
)

