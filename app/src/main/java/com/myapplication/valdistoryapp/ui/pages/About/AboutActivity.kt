package com.myapplication.valdistoryapp.ui.pages.About

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.myapplication.valdistoryapp.R
import com.myapplication.valdistoryapp.databinding.ActivityAboutBinding

class AboutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAboutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupData()
        setupAction()
    }

    private fun setupData() {
        Glide.with(this)
            .load("https://avatars.githubusercontent.com/u/66946020?v=4")
            .apply(
                RequestOptions
                    .placeholderOf(R.drawable.placeholder_img)
                    .error(R.drawable.error_img)
            )
            .into(binding.ivAuthorAvatar)
    }

    private fun setupAction() {
        binding.btnGithub.setOnClickListener {
            openWebPage("https://github.com/ValdiANS")
        }
    }

    private fun openWebPage(url: String) {
        val webpage: Uri = Uri.parse(url)
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = webpage

        startActivity(intent)
    }
}