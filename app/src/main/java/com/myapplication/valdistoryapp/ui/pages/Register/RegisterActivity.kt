package com.myapplication.valdistoryapp.ui.pages.Register

import android.animation.ObjectAnimator
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.BounceInterpolator
import android.widget.Button
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.myapplication.valdistoryapp.R
import com.myapplication.valdistoryapp.data.remote.response.GeneralStoryResponse
import com.myapplication.valdistoryapp.databinding.ActivityRegisterBinding
import com.myapplication.valdistoryapp.ui.ViewModelFactory
import com.myapplication.valdistoryapp.utils.afterTextChanged
import com.myapplication.valdistoryapp.utils.showSnackbar
import kotlinx.coroutines.launch
import retrofit2.HttpException

class RegisterActivity : AppCompatActivity() {

    private val viewModel by viewModels<RegisterViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
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
            btnSignIn.setOnClickListener {
                finish()
            }

            btnSignUp.setOnClickListener {
                signUpClickListener()
            }
        }
    }

    private fun setupFormValidation() {
        binding.run {
            edRegisterName.afterTextChanged { _ ->
                setupBtnState(btnSignUp)
            }

            edRegisterEmail.afterTextChanged { _ ->
                setupBtnState(btnSignUp)
            }

            edRegisterPassword.afterTextChanged { _ ->
                setupBtnState(btnSignUp)
            }
        }
    }

    private fun setupBtnState(btn: Button) {
        binding.run {
            btn.isEnabled =
                nameInputLayout.error.isNullOrEmpty() and
                        emailInputLayout.error.isNullOrEmpty() and
                        passwordInputLayout.error.isNullOrEmpty() and
                        (edRegisterName.text?.isNotEmpty() == true) and
                        (edRegisterEmail.text?.isNotEmpty() == true) and
                        (edRegisterPassword.text?.isNotEmpty() == true)
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

    // Sign Up Click Listener
    private fun signUpClickListener() {
        binding.progressLinear.show()
        binding.btnSignUp.isEnabled = false

        val name = binding.edRegisterName.text.toString()
        val email = binding.edRegisterEmail.text.toString()
        val password = binding.edRegisterPassword.text.toString()

        lifecycleScope.launch {
            try {
                viewModel.register(name, email, password)

                setResult(REGISTER_RESULT)
                finish()
            } catch (e: HttpException) {
                val responseErrorBody = Gson().fromJson(
                    e.response()?.errorBody()?.string(),
                    GeneralStoryResponse::class.java
                )

                showSnackbar(binding.btnSignUp, responseErrorBody.message)
            } catch (e: Exception) {
                showSnackbar(
                    binding.btnSignUp,
                    getString(R.string.offline_error_msg)
                )
            } finally {
                binding.progressLinear.hide()
                binding.btnSignUp.isEnabled = true
            }
        }
    }

    companion object {
        const val REGISTER_RESULT = 200
    }
}