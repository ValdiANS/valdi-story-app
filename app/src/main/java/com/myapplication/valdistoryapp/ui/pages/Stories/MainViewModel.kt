package com.myapplication.valdistoryapp.ui.pages.Stories

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.myapplication.valdistoryapp.data.local.entity.StoryEntity
import com.myapplication.valdistoryapp.data.local.pref.UserModel
import com.myapplication.valdistoryapp.data.repository.StoryRepository
import com.myapplication.valdistoryapp.data.repository.UserRepository

class MainViewModel(
    private val userRepository: UserRepository,
    private val storyRepository: StoryRepository
) : ViewModel() {

    fun getSession(): LiveData<UserModel> = userRepository.getSession().asLiveData()

    suspend fun logout() = userRepository.logout()

    fun getAllStories(): LiveData<PagingData<StoryEntity>> =
        storyRepository.getAllStories().cachedIn(viewModelScope)
}