package com.fumiya.flutter_camera_sample

import android.content.ContextWrapper
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import androidx.annotation.NonNull
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel

class MainActivity : FlutterActivity() {

    override fun configureFlutterEngine(@NonNull flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL).setMethodCallHandler { call, result ->
            // ここで Flutter 側で設定した `getBatteryLevel` を call からキャッチする
            // call は flutter 側で設定されたメソッド名を判別する
            // result で flutter 側に値を返却する
            if (call.method == METHOD_NAME_GET_BATTERY_LEVEL) {
                invokeGetBatteryLevel(result)
            } else {
                result.notImplemented()
            }
        }
    }

    private fun invokeGetBatteryLevel(result: MethodChannel.Result) {
        val batteryLevel = getBatteryLevel()
        if (batteryLevel != -1) {
            result.success(batteryLevel)
        } else {
            result.error("UNAVAILABLE", "Battery level not available.", null)
        }
    }

    private fun getBatteryLevel(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val batteryManager = getSystemService(BATTERY_SERVICE) as BatteryManager
            batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
        } else {
            val intent = ContextWrapper(applicationContext)
                    .registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
            intent!!.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) * 100 / intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        }
    }

    companion object {
        private const val CHANNEL = "com.fumiya.flutter_camera_sample/battery"

        private const val METHOD_NAME_GET_BATTERY_LEVEL = "getBatteryLevel"
    }
}
