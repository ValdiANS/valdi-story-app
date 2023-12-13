package com.myapplication.valdistoryapp.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.myapplication.valdistoryapp.data.repository.StoryRepository
import com.myapplication.valdistoryapp.data.repository.UserRepository
import com.myapplication.valdistoryapp.di.Injection
import com.myapplication.valdistoryapp.ui.pages.Login.LoginViewModel
import com.myapplication.valdistoryapp.ui.pages.Maps.MapsViewModel
import com.myapplication.valdistoryapp.ui.pages.PostStory.PostStoryViewModel
import com.myapplication.valdistoryapp.ui.pages.Register.RegisterViewModel
import com.myapplication.valdistoryapp.ui.pages.Stories.MainViewModel

class ViewModelFactory(
    private val userRepository: UserRepository,
    private val storyRepository: StoryRepository
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(userRepository, storyRepository) as T
            }

            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(userRepository) as T
            }

            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> {
                RegisterViewModel(userRepository) as T
            }

            modelClass.isAssignableFrom(PostStoryViewModel::class.java) -> {
                PostStoryViewModel(storyRepository) as T
            }

            modelClass.isAssignableFrom(MapsViewModel::class.java) -> {
                MapsViewModel(storyRepository) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null

        @JvmStatic
        fun getInstance(context: Context): ViewModelFactory =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: ViewModelFactory(
                    Injection.provideUserRepository(context),
                    Injection.provideStoryRepository(context)
                )
            }.also { INSTANCE = it }
    }
}