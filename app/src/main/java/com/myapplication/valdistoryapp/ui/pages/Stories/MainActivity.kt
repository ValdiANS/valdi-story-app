package com.myapplication.valdistoryapp.ui.pages.Stories

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.myapplication.valdistoryapp.R
import com.myapplication.valdistoryapp.data.local.pref.UserModel
import com.myapplication.valdistoryapp.databinding.ActivityMainBinding
import com.myapplication.valdistoryapp.ui.ViewModelFactory
import com.myapplication.valdistoryapp.ui.pages.About.AboutActivity
import com.myapplication.valdistoryapp.ui.pages.Login.LoginActivity
import com.myapplication.valdistoryapp.ui.pages.Maps.MapsActivity
import com.myapplication.valdistoryapp.ui.pages.PostStory.PostStoryActivity
import com.myapplication.valdistoryapp.utils.hide
import com.myapplication.valdistoryapp.utils.moveToActivity
import com.myapplication.valdistoryapp.utils.show
import com.myapplication.valdistoryapp.utils.showSnackbar
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private lateinit var binding: ActivityMainBinding

    private val launcherPostStory = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == PostStoryActivity.POST_STORY_RESULT) {
            getStoriesData()
            showSnackbar(binding.root, getString(R.string.story_posted))

            binding.rvStories.smoothScrollToPosition(0)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupObserver()
        setupAction()
    }

    private fun setupRecyclerView() {
        val layoutManager = LinearLayoutManager(this)
        binding.rvStories.layoutManager = layoutManager
    }

    private fun setupObserver() {
        viewModel.getSession().observe(this) { user ->
            redirectUserIfNotLoggedIn(user)
        }

        getStoriesData()
    }

    private fun setupAction() {
        binding.run {
            mainTopAppBar.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.action_language -> {
                        startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                        true
                    }

                    R.id.action_logout -> {
                        logoutHandler()
                        true
                    }

                    R.id.action_about -> {
                        moveToActivity(AboutActivity::class.java, false)
                        true
                    }

                    R.id.action_map -> {
                        moveToActivity(MapsActivity::class.java)
                        true
                    }

                    else -> false
                }
            }

            fabAddStory.setOnClickListener {
                val postStoryIntent = Intent(this@MainActivity, PostStoryActivity::class.java)
                launcherPostStory.launch(postStoryIntent)
            }
        }
    }

    private fun redirectUserIfNotLoggedIn(user: UserModel) {
        val content: View = findViewById(android.R.id.content)

        content.viewTreeObserver.addOnPreDrawListener(
            object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    showLoading(true)

                    return if (user.token.isEmpty()) {
                        val loginIntent = Intent(this@MainActivity, LoginActivity::class.java)

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            startActivity(
                                loginIntent,
                                ActivityOptionsCompat.makeSceneTransitionAnimation(this@MainActivity)
                                    .toBundle()
                            )
                        } else {
                            startActivity(loginIntent)
                        }

                        content.viewTreeObserver.removeOnPreDrawListener(this)

                        showLoading(false)
                        finish()
                        true
                    } else {
                        showLoading(false)
                        true
                    }
                }
            }
        )
    }

    private fun logoutHandler() {
        lifecycleScope.launch {
            try {
                viewModel.logout()
            } catch (e: Exception) {
                Log.d(TAG, "Logout failed: ${e.message}")

                showSnackbar(binding.root, e.message.toString())
            }
        }
    }

    private fun getStoriesData() {
        val adapter = StoriesAdapter()
        binding.rvStories.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                adapter.retry()
            }
        )

        viewModel.getAllStories().observe(this) {
            adapter.submitData(lifecycle, it)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        when (isLoading) {
            true -> {
                binding.progressIndicator.show()
                binding.rvStories.hide()
            }

            false -> {
                binding.progressIndicator.hide()
                binding.rvStories.show()
            }
        }
    }

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }
}