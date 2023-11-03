package com.myapplication.valdistoryapp.ui.pages.Login

import androidx.lifecycle.ViewModel
import com.myapplication.valdistoryapp.data.repository.UserRepository

class LoginViewModel(private val userRepository: UserRepository) : ViewModel() {
    suspend fun login(email: String, password: String) = userRepository.login(email, password)
}