package com.myapplication.valdistoryapp.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.myapplication.valdistoryapp.data.StoriesRemoteMediator
import com.myapplication.valdistoryapp.data.local.entity.StoryEntity
import com.myapplication.valdistoryapp.data.local.room.StoryDao
import com.myapplication.valdistoryapp.data.local.room.StoryDatabase
import com.myapplication.valdistoryapp.data.remote.response.GeneralStoryResponse
import com.myapplication.valdistoryapp.data.remote.retrofit.ApiService
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class StoryRepository private constructor(
    private val apiService: ApiService,
    private val storyDao: StoryDao,
    private val db: StoryDatabase
) {
    @OptIn(ExperimentalPagingApi::class)
    fun getAllStories(): LiveData<PagingData<StoryEntity>> {
        return Pager(
            config = PagingConfig(
                pageSize = 15
            ),

            remoteMediator = StoriesRemoteMediator(db, apiService),

            pagingSourceFactory = {
                storyDao.getAllStoriesPaging()
            }
        ).liveData
    }

    suspend fun getAllStoriesWithLocation(): List<StoryEntity>? {
        try {
            val response = apiService.getAllStories(page = null, size = null, location = 1)
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

            return formattedStoryList
        } catch (e: Exception) {
            Log.d("StoryRepository", "getAllStoriesWithLocation: ${e.message.toString()}")
            return null
        }
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
            storyDao: StoryDao,
            storyDb: StoryDatabase
        ): StoryRepository =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: StoryRepository(
                    apiService,
                    storyDao,
                    storyDb
                )
            }.also { INSTANCE = it }
    }
}