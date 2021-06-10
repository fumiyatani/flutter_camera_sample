package com.fumiya.flutter_camera_sample.camera

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.fumiya.flutter_camera_sample.R
import com.fumiya.flutter_camera_sample.databinding.ActivityCameraBinding

class CameraActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCameraBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_camera)

        setupTakePictureButton()
    }

    private fun setupTakePictureButton() {
        binding.takePictureButton.setOnClickListener {
            // todo カメラ撮影をすること
            val intent = Intent().apply {
                data = Uri.EMPTY
            }
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

    companion object {
        fun createIntent(context: Context) =
                Intent(context, CameraActivity::class.java)
    }
}