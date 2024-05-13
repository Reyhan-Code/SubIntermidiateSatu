package com.dicoding.subintermidiatesatu.view.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.subintermidiatesatu.data.Result
import com.dicoding.subintermidiatesatu.data.UserRepository
import com.dicoding.subintermidiatesatu.response.UserRegisterResponse
import kotlinx.coroutines.launch

class SignupViewModel(private val storyRepository: UserRepository) : ViewModel() {

    private val _registrationResult = MutableLiveData<Result<UserRegisterResponse>>()
    val registrationResult: LiveData<Result<UserRegisterResponse>> get() = _registrationResult

    fun register(name: String, email: String, password: String) {
        _registrationResult.value = Result.Loading

        viewModelScope.launch {
            val result = storyRepository.register(name, email, password)
            _registrationResult.value = result
        }
    }
}