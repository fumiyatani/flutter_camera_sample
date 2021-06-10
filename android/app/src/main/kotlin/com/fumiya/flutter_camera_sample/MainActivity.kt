package com.fumiya.flutter_camera_sample

import android.app.Activity
import android.content.Intent
import androidx.annotation.NonNull
import com.fumiya.flutter_camera_sample.camera.CameraActivity
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugins.GeneratedPluginRegistrant

class MainActivity : FlutterActivity() {

    /**
     * カメラ画面から戻ってきた際に Flutter 側にデータを渡すためにメンバ変数で Result を持っておく
     */
    private lateinit var cameraResult: MethodChannel.Result

    override fun configureFlutterEngine(@NonNull flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        GeneratedPluginRegistrant.registerWith(flutterEngine)
        startCameraActivity(flutterEngine)
    }

    private fun startCameraActivity(flutterEngine: FlutterEngine) {
        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL).setMethodCallHandler { call, result ->
            cameraResult = result
            // ここで Flutter 側で設定した `getBatteryLevel` を call からキャッチする
            // call は flutter 側で設定されたメソッド名を判別する
            // result で flutter 側に値を返却する
            if (call.method == METHOD_NAME_GET_BATTERY_LEVEL) {
                val intent = CameraActivity.createIntent(this)
                startActivityForResult(intent, REQUEST_CODE_CAMERA)
            } else {
                cameraResult.notImplemented()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK) {
            cameraResult.error("failed", "failed to get image path", null)
            return
        }

        if (requestCode == REQUEST_CODE_CAMERA) {
            cameraResult.success(data?.data)
        }
    }

    companion object {
        private const val CHANNEL = "com.fumiya.flutter_camera_sample/camera"
        private const val METHOD_NAME_GET_BATTERY_LEVEL = "moveToCameraActivity"
        private const val REQUEST_CODE_CAMERA = 100
    }
}
