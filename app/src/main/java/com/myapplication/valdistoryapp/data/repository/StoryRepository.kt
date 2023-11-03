package com.myapplication.valdistoryapp.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import com.myapplication.valdistoryapp.data.ResultState
import com.myapplication.valdistoryapp.data.local.entity.StoryEntity
import com.myapplication.valdistoryapp.data.local.room.StoryDao
import com.myapplication.valdistoryapp.data.remote.response.GeneralStoryResponse
import com.myapplication.valdistoryapp.data.remote.retrofit.ApiService
import com.myapplication.valdistoryapp.utils.OFFLINE_ERROR_CODE
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class StoryRepository private constructor(
    private val apiService: ApiService,
    private val storyDao: StoryDao
) {
    fun getAllStories(): LiveData<ResultState<List<StoryEntity>>> = liveData {
        emit(ResultState.Loading)

        try {
            val response = apiService.getAllStories()
            val storyList = response.listStory

            val formattedStoryList = storyList.map { storyItem ->
                StoryEntity(
                    storyItem.id,
                    storyItem.name,
                    storyItem.description,
                    storyItem.photoUrl,
                    storyItem.createdAt,
                    storyItem.lat,
                    storyItem.lon,
                )
            }

            storyDao.insertAllStories(formattedStoryList)

        } catch (e: Exception) {
            Log.d("StoryRepository", "getAllStories: ${e.message.toString()}")
            emit(ResultState.Error(OFFLINE_ERROR_CODE))
        }

        val localData: LiveData<ResultState<List<StoryEntity>>> =
            storyDao.getAllStories().map { ResultState.Success(it) }

        emitSource(localData)
    }

    suspend fun getDetailStory(id: String): StoryEntity {
        val response = apiService.getDetailStory(id)
        val detailStory = response.story

        return detailStory.run {
            StoryEntity(
                id, name, description, photoUrl, createdAt, lat, lon
            )
        }
    }

    suspend fun postStory(
        description: String,
        imgFile: File,
        lat: Double? = null,
        lon: Double? = null,
    ): GeneralStoryResponse {
        val descriptionReqBody = description.toRequestBody("text/plain".toMediaType())
        val latReqBody = lat?.toString()?.toRequestBody("text/plain".toMediaType())
        val lonReqBody = lon?.toString()?.toRequestBody("text/plain".toMediaType())
        val imgFileReqBody = imgFile.asRequestBody("image/jpeg".toMediaType())

        val imgMultipartBody = MultipartBody.Part.createFormData(
            "photo",
            imgFile.name,
            imgFileReqBody
        )

        val response = apiService.postStory(
            imgMultipartBody,
            descriptionReqBody,
            latReqBody,
            lonReqBody
        )

        return response
    }

    companion object {
        @Volatile
        private var INSTANCE: StoryRepository? = null

        fun getInstance(
            apiService: ApiService,
            storyDao: StoryDao
        ): StoryRepository =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: StoryRepository(
                    apiService,
                    storyDao
                )
            }.also { INSTANCE = it }
    }
}