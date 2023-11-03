package com.myapplication.valdistoryapp.ui.pages.Login

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.BounceInterpolator
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.myapplication.valdistoryapp.R
import com.myapplication.valdistoryapp.data.remote.response.GeneralStoryResponse
import com.myapplication.valdistoryapp.databinding.ActivityLoginBinding
import com.myapplication.valdistoryapp.ui.ViewModelFactory
import com.myapplication.valdistoryapp.ui.pages.Register.RegisterActivity
import com.myapplication.valdistoryapp.ui.pages.Stories.MainActivity
import com.myapplication.valdistoryapp.utils.afterTextChanged
import com.myapplication.valdistoryapp.utils.moveToActivity
import com.myapplication.valdistoryapp.utils.showSnackbar
import kotlinx.coroutines.launch
import retrofit2.HttpException

class LoginActivity : AppCompatActivity() {

    private val viewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityLoginBinding

    private val launcherRegister = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RegisterActivity.REGISTER_RESULT) {
            showSnackbar(binding.root, getString(R.string.registration_successful))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()
        setupFormValidation()
        playAnimation()
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupAction() {
        binding.run {
            btnSignUp.setOnClickListener {
                val registerIntent = Intent(this@LoginActivity, RegisterActivity::class.java)
                launcherRegister.launch(registerIntent)
            }

            btnSignIn.setOnClickListener {
                signInClickListener()
            }
        }
    }

    private fun setupFormValidation() {
        binding.run {
            edLoginEmail.afterTextChanged { _ ->
                setupBtnState(btnSignIn)
            }

            edLoginPassword.afterTextChanged { _ ->
                setupBtnState(btnSignIn)
            }
        }
    }

    private fun setupBtnState(btn: Button) {
        binding.run {
            btn.isEnabled =
                emailInputLayout.error.isNullOrEmpty() and
                        passwordInputLayout.error.isNullOrEmpty() and
                        (edLoginEmail.text?.isNotEmpty() == true) and
                        (edLoginPassword.text?.isNotEmpty() == true)
        }
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(
            binding.ivStormIcon1,
            View.TRANSLATION_Y,
            0f,
            200f
        ).apply {
            duration = 5000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
            interpolator = AccelerateDecelerateInterpolator()

            start()
        }

        ObjectAnimator.ofFloat(
            binding.ivStormIcon1,
            View.ROTATION,
            0f,
            -720f
        ).apply {
            duration = 2500
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.RESTART

            start()
        }

        ObjectAnimator.ofFloat(
            binding.ivStormIcon2,
            View.TRANSLATION_Y,
            0f,
            -200f
        ).apply {
            duration = 5000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
            interpolator = BounceInterpolator()

            start()
        }

        ObjectAnimator.ofFloat(
            binding.ivStormIcon2,
            View.ROTATION,
            0f,
            -720f
        ).apply {
            duration = 2500
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.RESTART

            start()
        }
    }

    // Sign In Click Listener
    private fun signInClickListener() {
        binding.progressLinear.show()
        binding.btnSignIn.isEnabled = false

        val email = binding.edLoginEmail.text.toString()
        val password = binding.edLoginPassword.text.toString()

        lifecycleScope.launch {
            try {
                viewModel.login(email, password)

                showSnackbar(binding.btnSignIn, getString(R.string.login_successful))
                moveToActivity(MainActivity::class.java, false)
                finish()
            } catch (e: HttpException) {
                val responseErrorBody = Gson().fromJson(
                    e.response()?.errorBody()?.string(),
                    GeneralStoryResponse::class.java
                )

                showSnackbar(binding.btnSignIn, responseErrorBody.message)
            } catch (e: Exception) {
                showSnackbar(
                    binding.btnSignIn,
                    getString(R.string.offline_error_msg)
                )
            } finally {
                binding.progressLinear.hide()
                binding.btnSignIn.isEnabled = true
            }
        }
    }
}