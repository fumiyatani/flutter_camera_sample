import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

class PlatformCameraPage extends StatefulWidget {
  @override
  State<StatefulWidget> createState() => _PlatformCameraPageState();
}

class _PlatformCameraPageState extends State<PlatformCameraPage> {
  // 各プラットフォームと接続するためのエントリーポイント
  static const platform = MethodChannel('com.fumiya.flutter_camera_sample/battery');

  String _batteryLevel = 'Unknown battery level.';

  Future<void> _getBatteryLevel() async {
    String batteryLevel;

    try {
      // プラットフォーム側に公開する Flutter 側のメソッド
      // MethodChannel#invokeMethod<ReturnType>('Method Name');
      final result = await platform.invokeMethod<int?>('getBatteryLevel');
      batteryLevel = 'Battery level at $result % .';
    } on PlatformException catch (e) {
      batteryLevel = "Failed to get battery level: '${e.message}'.";
    }

    // プラットフォームから取得したデータを画面に反映する
    setState(() {
      _batteryLevel = batteryLevel;
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('Battery Level'),
      ),
      body: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Text(_batteryLevel),
        ],
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: () => _getBatteryLevel,
        child: Icon(Icons.ac_unit_sharp),
      ),
    );
  }
}
