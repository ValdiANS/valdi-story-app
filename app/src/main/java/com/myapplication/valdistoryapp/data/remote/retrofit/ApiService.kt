package com.myapplication.valdistoryapp.data.remote.retrofit

import com.myapplication.valdistoryapp.data.remote.response.DetailStoryResponse
import com.myapplication.valdistoryapp.data.remote.response.GeneralStoryResponse
import com.myapplication.valdistoryapp.data.remote.response.GetAllStoriesResponse
import com.myapplication.valdistoryapp.data.remote.response.LoginResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @FormUrlEncoded
    @POST("/v1/register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): GeneralStoryResponse

    @FormUrlEncoded
    @POST("/v1/login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse

    @Multipart
    @POST("/v1/stories")
    suspend fun postStory(
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: RequestBody? = null,
        @Part("lon") lon: RequestBody? = null
    ): GeneralStoryResponse

    @GET("/v1/stories")
    suspend fun getAllStories(
        @Query("page") page: Int? = 1,
        @Query("size") size: Int? = 15,
        @Query("location") location: Int? = 0,
    ): GetAllStoriesResponse

    @GET("/v1/stories/{id}")
    suspend fun getDetailStory(
        @Path("id") id: String
    ): DetailStoryResponse
}