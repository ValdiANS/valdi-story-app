package com.myapplication.valdistoryapp.ui.pages.Stories

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.myapplication.valdistoryapp.R
import com.myapplication.valdistoryapp.data.local.entity.StoryEntity
import com.myapplication.valdistoryapp.databinding.StoryCardItemBinding
import com.myapplication.valdistoryapp.ui.pages.DetailStory.DetailStoryActivity
import com.myapplication.valdistoryapp.utils.withDateFormat

class StoriesAdapter() : PagingDataAdapter<StoryEntity, StoriesAdapter.ViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            StoryCardItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val storyItem = getItem(position)

        if (storyItem != null) {
            holder.bind(storyItem)
        }
    }

    class ViewHolder(val binding: StoryCardItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(story: StoryEntity) {
            binding.tvUserMonogram.text = story.name[0].toString().uppercase()
            binding.tvItemName.text = story.name
            binding.tvItemDate.text = story.createdAt.withDateFormat()

            Glide.with(itemView.context)
                .load(story.photoUrl)
                .apply(
                    RequestOptions.placeholderOf(R.drawable.placeholder_img)
                        .error(R.drawable.error_img)
                )
                .into(binding.ivItemPhoto)

            binding.storyCard.setOnClickListener {
                storyCardClickListener(story)
            }
        }

        private fun storyCardClickListener(story: StoryEntity) {
            // Move to detail story activity with data
            val detailStoryIntent = Intent(itemView.context, DetailStoryActivity::class.java)
            detailStoryIntent.putExtra(DetailStoryActivity.EXTRA_STORY, story)

            val optionsCompat: ActivityOptionsCompat =
                ActivityOptionsCompat.makeSceneTransitionAnimation(
                    itemView.context as Activity,
                    Pair(binding.ivItemPhoto, "storyImage"),
                    Pair(binding.userInfoContainer, "userInfo"),
                )

            itemView.context.startActivity(detailStoryIntent, optionsCompat.toBundle())
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<StoryEntity>() {
            override fun areItemsTheSame(
                oldItem: StoryEntity,
                newItem: StoryEntity
            ): Boolean =
                oldItem == newItem

            override fun areContentsTheSame(
                oldItem: StoryEntity,
                newItem: StoryEntity
            ): Boolean =
                oldItem == newItem
        }
    }
}