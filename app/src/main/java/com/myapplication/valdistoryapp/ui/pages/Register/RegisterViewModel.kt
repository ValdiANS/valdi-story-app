package com.myapplication.valdistoryapp.ui.pages.Register

import androidx.lifecycle.ViewModel
import com.myapplication.valdistoryapp.data.repository.UserRepository

class RegisterViewModel(private val userRepository: UserRepository) : ViewModel() {
    suspend fun register(name: String, email: String, password: String) =
        userRepository.register(name, email, password)
}