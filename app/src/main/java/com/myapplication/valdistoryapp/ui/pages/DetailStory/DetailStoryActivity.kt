package com.myapplication.valdistoryapp.ui.pages.DetailStory

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.myapplication.valdistoryapp.R
import com.myapplication.valdistoryapp.data.local.entity.StoryEntity
import com.myapplication.valdistoryapp.databinding.ActivityDetailStoryBinding
import com.myapplication.valdistoryapp.utils.withDateFormat

class DetailStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailStoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupData()
        setupAction()
    }

    private fun setupData() {
        val story = intent.getParcelableExtra(EXTRA_STORY, StoryEntity::class.java)

        Glide.with(this)
            .load(story?.photoUrl)
            .apply(
                RequestOptions
                    .placeholderOf(R.drawable.placeholder_img)
                    .error(R.drawable.error_img)
            )
            .into(binding.ivItemPhoto)

        binding.tvUserMonogram.text = story?.name?.get(0).toString().uppercase()
        binding.tvItemName.text = story?.name
        binding.tvItemDate.text = story?.createdAt?.withDateFormat()
        binding.tvDescription.text = story?.description
    }

    private fun setupAction() {
        binding.topAppBar.setNavigationOnClickListener {
            finishAfterTransition()
        }
    }

    companion object {
        const val EXTRA_STORY = "Story"
    }
}