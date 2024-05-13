package com.dicoding.subintermidiatesatu.view.upload

import androidx.lifecycle.ViewModel
import com.dicoding.subintermidiatesatu.data.UserRepository
import java.io.File

class UploadViewModel(private val repository: UserRepository) : ViewModel() {

    fun uploadStory(file: File, description: String, lat: Double? = null, lon: Double? = null) =
        repository.uploadStories(file, description, lat, lon)
}