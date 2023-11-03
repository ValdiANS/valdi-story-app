package com.myapplication.valdistoryapp.di

import android.content.Context
import com.myapplication.valdistoryapp.data.local.pref.UserPreference
import com.myapplication.valdistoryapp.data.local.pref.dataStore
import com.myapplication.valdistoryapp.data.local.room.StoryDatabase
import com.myapplication.valdistoryapp.data.remote.retrofit.ApiConfig
import com.myapplication.valdistoryapp.data.repository.StoryRepository
import com.myapplication.valdistoryapp.data.repository.UserRepository

object Injection {
    fun provideStoryRepository(context: Context): StoryRepository {
        val storyDb = StoryDatabase.getInstance(context)
        val storyDao = storyDb.storyDao()
        val apiService = ApiConfig.getApiService(context)

        return StoryRepository.getInstance(apiService, storyDao)
    }

    fun provideUserRepository(context: Context): UserRepository {
        val userPref = UserPreference.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService(context)

        return UserRepository.getInstance(userPref, apiService)
    }
}