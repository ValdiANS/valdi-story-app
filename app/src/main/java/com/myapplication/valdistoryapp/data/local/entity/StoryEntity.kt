package com.myapplication.valdistoryapp.data.local.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "story")
@Parcelize
data class StoryEntity(
    @PrimaryKey
    val id: String,

    @ColumnInfo(name = "name")
    val name: String = "",

    @ColumnInfo(name = "description")
    val description: String = "",

    @ColumnInfo(name = "photoUrl")
    val photoUrl: String = "",

    @ColumnInfo(name = "createdAt")
    val createdAt: String = "",

    @ColumnInfo(name = "lat")
    val lat: Double?,

    @ColumnInfo(name = "lon")
    val lon: Double?
) : Parcelable