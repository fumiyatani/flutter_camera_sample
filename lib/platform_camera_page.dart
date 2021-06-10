import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

class PlatformCameraPage extends StatefulWidget {
  @override
  State<StatefulWidget> createState() => _PlatformCameraPageState();
}

class _PlatformCameraPageState extends State<PlatformCameraPage> {
  // 各プラットフォームと接続するためのエントリーポイント
  static const cameraPlatform = MethodChannel('com.fumiya.flutter_camera_sample/camera');

  String? _imagePath;

  Future<void> _getBatteryLevel() async {
    String? imagePath;

    try {
      // プラットフォーム側に公開する Flutter 側のメソッド
      // MethodChannel#invokeMethod<ReturnType>('Method Name');
      imagePath = await cameraPlatform.invokeMethod<String?>('moveToCameraActivity');
    } on PlatformException catch (e) {
      imagePath = 'failed to open CameraActivity';
    }

    // プラットフォームから取得したデータを画面に反映する
    setState(() {
      _imagePath = imagePath;
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('Camera Sample'),
      ),
      body: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Text('この下にImagePathが表示されます'),
          _imagePath == null ? Text('null') : Text('imagePath : $_imagePath'),
        ],
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: () async {
          await _getBatteryLevel();
        },
        child: Icon(Icons.ac_unit_sharp),
      ),
    );
  }
}
