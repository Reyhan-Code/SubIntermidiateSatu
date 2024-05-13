package com.dicoding.subintermidiatesatu.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.dicoding.subintermidiatesatu.data.pref.UserModel
import com.dicoding.subintermidiatesatu.data.pref.UserPreference
import com.dicoding.subintermidiatesatu.data.room.ApiConfig
import com.dicoding.subintermidiatesatu.data.room.ApiService
import com.dicoding.subintermidiatesatu.response.ErrorResponse
import com.dicoding.subintermidiatesatu.response.ListStoryItem
import com.dicoding.subintermidiatesatu.response.UserLoginResponse
import com.dicoding.subintermidiatesatu.response.UserRegisterResponse
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File


class UserRepository private constructor(
    private val apiService: ApiService,
    private val userPreference: UserPreference
) {

    private suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun logout() {
        userPreference.logout()
    }

    suspend fun register(
        name: String,
        email: String,
        password: String
    ): Result<UserRegisterResponse> {
        Result.Loading
        return try {
            val response = apiService.register(name, email, password)
            if (response.error == true) {
                Result.Error(response.message ?: "Unknown error")
            } else {
                Result.Success(response)
            }
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
            val errorMessage = errorBody.message
            Result.Error(errorMessage.toString())
        }
    }

    suspend fun login(email: String, password: String): Result<UserLoginResponse> {
        return try {
            val response = apiService.login(email, password)
            val session = UserModel(
                name = response.loginResult.name,
                email = email,
                token = response.loginResult.token,
                isLogin = true
            )
            saveSession(session)
            ApiConfig.token = response.loginResult.token
            Result.Success(response)

        } catch (e: HttpException) {
            val errorMessage = e.response()?.errorBody()?.string()
            Result.Error(errorMessage ?: "An error occurred")
        }
    }

    fun getStories(): LiveData<Result<List<ListStoryItem>>> =
        liveData {
            emit(Result.Loading)
            try {
                val response = apiService.getStories()
                val nonNullList = response.listStory?.mapNotNull { it } ?: emptyList()
                emit(Result.Success(nonNullList))
            } catch (e: HttpException) {
                val error = e.response()?.errorBody()?.string()
                val body = Gson().fromJson(error, ErrorResponse::class.java)
                emit(Result.Error(body?.message ?: "Error"))
            }
        }

    fun uploadStories(imageFile: File, description: String, lat: Double?, lon: Double?) = liveData {
        emit(Result.Loading)
        val requestBody = description.toRequestBody("text/plain".toMediaType())
        val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
        val multipartBody = MultipartBody.Part.createFormData(
            "photo",
            imageFile.name,
            requestImageFile
        )
        val requestLat = lat?.toString()?.toRequestBody()
        val requestLon = lon?.toString()?.toRequestBody()
        try {
            val successResponse =
                apiService.uploadImage(multipartBody, requestBody, requestLat, requestLon)
            if (successResponse.error) {
                emit(Result.Error(successResponse.message))
            } else {
                emit(Result.Success(successResponse))
            }
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
            val errorMessage = errorBody.message
            emit(Result.Error(errorMessage.toString()))
        }
    }


    companion object {
        fun getInstance(
            apiService: ApiService,
            userPreference: UserPreference
        ): UserRepository = UserRepository(apiService, userPreference)
    }
}