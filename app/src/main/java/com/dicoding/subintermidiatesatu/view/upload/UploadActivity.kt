package com.dicoding.subintermidiatesatu.view.upload

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.dicoding.subintermidiatesatu.data.Result
import com.dicoding.subintermidiatesatu.R
import com.dicoding.subintermidiatesatu.data.getImageUri
import com.dicoding.subintermidiatesatu.data.reduceFileImage
import com.dicoding.subintermidiatesatu.data.uriToFile
import com.dicoding.subintermidiatesatu.databinding.ActivityUploadBinding
import com.dicoding.subintermidiatesatu.view.ViewModelFactory
import com.dicoding.subintermidiatesatu.view.main.MainActivity


class UploadActivity : AppCompatActivity() {


    private var currentImageUri: Uri? = null
    private lateinit var binding: ActivityUploadBinding

    private val viewModel by viewModels<UploadViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!isPermissionsGranted(Manifest.permission.CAMERA)) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }

        binding.btnGallery.setOnClickListener { startGallery() }
        binding.btnCamera.setOnClickListener { startCamera() }
        binding.btnUpload.setOnClickListener { uploadImage() }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(this, "Permission request accept", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Permission request denied", Toast.LENGTH_LONG).show()
            }
        }

    private fun isPermissionsGranted(permission: String) =
        ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED


    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.imgPreview.setImageURI(it)
        }
    }

    private fun startCamera() {
        currentImageUri = getImageUri(this)
        launcherIntentCamera.launch(currentImageUri)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        }
    }

    private fun uploadImage() {
        currentImageUri?.let {
            val image = uriToFile(it, this).reduceFileImage()
            val description = binding.addDescription.text.toString()

            viewModel.uploadStory(image, description).observe(this) { result ->
                if (result != null) {
                    when (result) {
                        is Result.Success -> {
                            binding.loadingProgressBar.visibility = View.GONE
                            Toast.makeText(this, R.string.upload_success, Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, MainActivity::class.java)
                            intent.flags =
                                Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                            finish()
                        }

                        is Result.Error -> {
                            binding.loadingProgressBar.visibility = View.GONE
                            Toast.makeText(this, R.string.upload_failed, Toast.LENGTH_SHORT).show()
                        }

                        is Result.Loading -> {
                            binding.loadingProgressBar.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }
    }

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }
}