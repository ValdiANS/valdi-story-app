package com.myapplication.valdistoryapp.ui.pages.Maps

import androidx.lifecycle.ViewModel
import com.myapplication.valdistoryapp.data.repository.StoryRepository

class MapsViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    suspend fun getAllStories() = storyRepository.getAllStoriesWithLocation()
}