package com.fumiya.flutter_camera_sample.camera

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.fumiya.flutter_camera_sample.R
import com.fumiya.flutter_camera_sample.databinding.ActivityCameraBinding
import permissions.dispatcher.*
import permissions.dispatcher.ktx.PermissionsRequester
import permissions.dispatcher.ktx.constructPermissionsRequest
import java.io.File
import java.io.IOException
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraActivity : AppCompatActivity() {

    private lateinit var cameraPermissionsRequester: PermissionsRequester

    private var imageCapture: ImageCapture? = null
    private lateinit var cameraExecutorService: ExecutorService

    private lateinit var binding: ActivityCameraBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        cameraPermissionsRequester = constructPermissionsRequest(
                Manifest.permission.CAMERA,
                onShowRationale = ::onCameraShowRationale,
                onPermissionDenied = ::onCameraDenied,
                onNeverAskAgain = ::onCameraNeverAskAgain,
                requiresPermission = ::startCameraPreview
        )
        cameraPermissionsRequester.launch()

        binding = DataBindingUtil.setContentView(this, R.layout.activity_camera)
        cameraExecutorService = Executors.newSingleThreadExecutor()
        setupTakePictureButton()
    }

    private fun setupTakePictureButton() {
        binding.takePictureButton.setOnClickListener {
            val intent = Intent()

            if (imageCapture == null) {
                intent.data = null
                setResult(Activity.RESULT_CANCELED, intent)
                finish()
            }

            val imageCapture = imageCapture!!

            // Create temporary file
            val outputDir = applicationContext.cacheDir
            val outputDirectory: File? = try {
                File.createTempFile("CAP_${Date().time}", ".jpg", outputDir)
            } catch (e: IOException) {
                intent.data = null
                setResult(Activity.RESULT_CANCELED, intent)
                finish()
                null
            } catch (e: SecurityException) {
                intent.data = null
                setResult(Activity.RESULT_CANCELED, intent)
                finish()
                null
            }

            if (outputDirectory == null) {
                intent.data = null
                setResult(Activity.RESULT_CANCELED, intent)
                finish()
            }
            val photoFile: File = outputDirectory!!
            val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

            imageCapture.takePicture(
                    outputOptions, ContextCompat.getMainExecutor(this), object : ImageCapture.OnImageSavedCallback {

                override fun onError(exc: ImageCaptureException) {
                    Log.e("TAG", "Photo capture failed: ${exc.message}", exc)
                    intent.data = null
                    setResult(Activity.RESULT_CANCELED, intent)
                    finish()
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)
                    val msg = "Photo capture succeeded: $savedUri"
                    Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                    Log.d("TAG", msg)

                    intent.data = savedUri
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }
            })
        }
    }

    private fun onCameraDenied() {
        Toast.makeText(this, "カメラの使用を許可してください", Toast.LENGTH_SHORT).show()
    }

    private fun onCameraShowRationale(request: PermissionRequest) {
        request.proceed()
    }

    private fun onCameraNeverAskAgain() {
        Toast.makeText(this, "カメラの使用を許可してください", Toast.LENGTH_SHORT).show()
    }

    private fun startCameraPreview() {
        // Previewの起動
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener(Runnable {
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                    .build()
                    .also { it.setSurfaceProvider(binding.preview.surfaceProvider) }

            // Capture
            imageCapture = ImageCapture.Builder()
                    .build()

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                        this, cameraSelector, preview, imageCapture)

            } catch (exc: Exception) {
                Log.e("TAG", "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    override fun onDestroy() {
        cameraExecutorService.shutdown()
        super.onDestroy()
    }

    companion object {
        fun createIntent(context: Context) =
                Intent(context, CameraActivity::class.java)
    }
}