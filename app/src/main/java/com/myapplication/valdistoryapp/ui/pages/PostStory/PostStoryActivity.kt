package com.myapplication.valdistoryapp.ui.pages.PostStory

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
import com.google.gson.Gson
import com.myapplication.valdistoryapp.R
import com.myapplication.valdistoryapp.data.remote.response.GeneralStoryResponse
import com.myapplication.valdistoryapp.databinding.ActivityPostStoryBinding
import com.myapplication.valdistoryapp.ui.ViewModelFactory
import com.myapplication.valdistoryapp.utils.afterTextChanged
import com.myapplication.valdistoryapp.utils.getImageUri
import com.myapplication.valdistoryapp.utils.reduceFileImage
import com.myapplication.valdistoryapp.utils.showSnackbar
import com.myapplication.valdistoryapp.utils.uriToFile
import kotlinx.coroutines.launch
import retrofit2.HttpException

class PostStoryActivity : AppCompatActivity() {

    private val viewModel by viewModels<PostStoryViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private lateinit var binding: ActivityPostStoryBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var currentImageUri: Uri? = null
    private var userCurrentLatitude: Double? = null
    private var userCurrentLongitude: Double? = null
    private var isAddingLocation: Boolean = false

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri !== null) {
            currentImageUri = uri
            showSelectedImage()
            setupBtnState(binding.btnUpload)
        } else {
            Log.d("Photo Picker", getString(R.string.no_media_selected))
            showSnackbar(binding.root, getString(R.string.no_media_selected))
        }
    }

    private val launcherCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showSelectedImage()
            setupBtnState(binding.btnUpload)
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                // Precise location access granted.
                getMyLocation()
            }

            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                // Only approximate location access granted.
                getMyLocation()
            }

            else -> {}
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAction()
        setupFormValidation()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        getMyLocation()
    }

    private fun setupAction() {
        binding.topAppBar.setNavigationOnClickListener {
            finish()
        }

        binding.btnCamera.setOnClickListener {
            // Open Camera
            currentImageUri = getImageUri(this)
            launcherCamera.launch(currentImageUri)
        }

        binding.btnGallery.setOnClickListener {
            // Start Gallery
            launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.checkboxLocation.setOnCheckedChangeListener { btnView, isChecked ->
            isAddingLocation = isChecked
        }

        binding.btnUpload.setOnClickListener {
            postStory()
        }
    }

    private fun setupFormValidation() {
        binding.run {
            edAddDescription.afterTextChanged { _ ->
                setupBtnState(btnUpload)
            }
        }
    }

    private fun setupBtnState(btn: Button) {
        btn.isEnabled =
            binding.descriptionInputLayout.error.isNullOrEmpty() and
                    (binding.edAddDescription.text?.isNotEmpty() == true) and
                    (currentImageUri !== null)
    }

    private fun showSelectedImage() {
        currentImageUri?.let {
            binding.ivPreviewImage.setImageURI(it)
        }
    }

    private fun postStory() {
        currentImageUri?.let { uri ->
            val imgFile = uriToFile(uri, this).reduceFileImage()
            val description = binding.edAddDescription.text.toString()

            showLoading(true)

            lifecycleScope.launch {
                try {
                    val response = if (isAddingLocation) {
                        viewModel.postStory(
                            description,
                            imgFile,
                            userCurrentLatitude,
                            userCurrentLongitude
                        )
                    } else {
                        viewModel.postStory(description, imgFile)
                    }

                    setResult(POST_STORY_RESULT)
                    finish()
                } catch (e: HttpException) {
                    val errorBody = e.response()?.errorBody()?.string()
                    val errorResponse = Gson().fromJson(errorBody, GeneralStoryResponse::class.java)

                    showSnackbar(binding.root, errorResponse.message)
                } catch (e: Exception) {
                    showSnackbar(binding.root, e.message.toString())
                } finally {
                    showLoading(false)
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressLinear.show()
        } else {
            binding.progressLinear.hide()
        }
    }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun getMyLocation() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {
            fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                object : CancellationToken() {
                    override fun onCanceledRequested(p0: OnTokenCanceledListener): CancellationToken {
                        return CancellationTokenSource().token
                    }

                    override fun isCancellationRequested(): Boolean = false
                })
                .addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        userCurrentLatitude = location.latitude
                        userCurrentLongitude = location.longitude
                    } else {
                        showSnackbar(binding.root, getString(R.string.location_not_found_error_msg))
                    }
                }

        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    companion object {
        const val POST_STORY_RESULT = 200
    }
}
