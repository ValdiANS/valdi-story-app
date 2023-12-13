package com.myapplication.valdistoryapp.data.local.room

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.myapplication.valdistoryapp.data.local.entity.StoryEntity

@Dao
interface StoryDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllStories(storyList: List<StoryEntity>)

    @Query("SELECT * FROM story ORDER BY createdAt DESC")
    fun getAllStories(): LiveData<List<StoryEntity>>

    @Query("SELECT * FROM story ORDER BY createdAt DESC")
    fun getAllStoriesPaging(): PagingSource<Int, StoryEntity>

    @Query("SELECT * FROM story WHERE lat IS NOT NULL AND lon IS NOT NULL")
    fun getAllStoriesWithLocation(): LiveData<List<StoryEntity>>

    @Query("DELETE FROM story")
    suspend fun deleteAll()
}