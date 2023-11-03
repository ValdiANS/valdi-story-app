package com.myapplication.valdistoryapp.ui.pages.PostStory

import androidx.lifecycle.ViewModel
import com.myapplication.valdistoryapp.data.remote.response.GeneralStoryResponse
import com.myapplication.valdistoryapp.data.repository.StoryRepository
import java.io.File

class PostStoryViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    suspend fun postStory(
        description: String,
        imgFile: File,
        lat: Double? = null,
        lon: Double? = null,
    ): GeneralStoryResponse = storyRepository.postStory(description, imgFile, lat, lon)
}