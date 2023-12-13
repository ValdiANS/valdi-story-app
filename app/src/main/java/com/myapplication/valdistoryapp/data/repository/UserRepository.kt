package com.myapplication.valdistoryapp.data.repository

import com.myapplication.valdistoryapp.data.local.pref.UserModel
import com.myapplication.valdistoryapp.data.local.pref.UserPreference
import com.myapplication.valdistoryapp.data.remote.response.GeneralStoryResponse
import com.myapplication.valdistoryapp.data.remote.response.LoginResponse
import com.myapplication.valdistoryapp.data.remote.retrofit.ApiService
import com.myapplication.valdistoryapp.utils.wrapEspressoIdlingResource
import kotlinx.coroutines.flow.Flow

class UserRepository private constructor(
    private val userPreference: UserPreference,
    private val apiService: ApiService
) {
    suspend fun register(name: String, email: String, password: String): GeneralStoryResponse {
        return apiService.register(name, email, password)
    }

    suspend fun login(email: String, password: String): LoginResponse {
//        wrapEspressoIdlingResource {

            val loginResponse = apiService.login(email, password)
            val loginResult = loginResponse.loginResult

            if (!loginResponse.error) {
                userPreference.saveSession(
                    UserModel(
                        loginResult.userId,
                        loginResult.name,
                        loginResult.token
                    )
                )
            }

            return loginResponse
//        }
    }

    fun getSession(): Flow<UserModel> = userPreference.getSession()

    suspend fun logout() = userPreference.logout()

    companion object {
        @Volatile
        private var INSTANCE: UserRepository? = null

        fun getInstance(userPreference: UserPreference, apiService: ApiService): UserRepository =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: UserRepository(userPreference, apiService)
            }.also { INSTANCE = it }
    }
}