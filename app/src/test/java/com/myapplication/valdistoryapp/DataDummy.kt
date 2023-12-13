package com.myapplication.valdistoryapp

import com.myapplication.valdistoryapp.data.local.entity.StoryEntity
import java.util.Date

object DataDummy {
    fun generateDummyStoriesResponse(): List<StoryEntity> {
        val items: MutableList<StoryEntity> = arrayListOf()

        for (i in 0..100) {
            val story = StoryEntity(
                i.toString(),
                "Story $i",
                "Story $i description",
                "story-$i-photo-url",
                Date().toString(),
                if (i % 2 == 0) i.toDouble() else null,
                if (i % 2 == 0) i.toDouble() else null,
            )

            items.add(story)
        }

        return items
    }
}