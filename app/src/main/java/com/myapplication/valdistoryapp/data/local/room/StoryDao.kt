package com.myapplication.valdistoryapp.data.local.room

import androidx.lifecycle.LiveData
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
}