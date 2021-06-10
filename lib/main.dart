import 'dart:async';

import 'package:flutter/material.dart';
import 'package:flutter_camera_sample/platform_camera_page.dart';

Future<void> main() async {
  runApp(
    MaterialApp(
      title: 'Flutter Demo',
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      home: TakePictureScreen(),
    ),
  );
}

class TakePictureScreen extends StatefulWidget {
  const TakePictureScreen({
    Key? key,
  }) : super(key: key);

  @override
  State<StatefulWidget> createState() => TakePictureScreenState();
}

class TakePictureScreenState extends State<TakePictureScreen> {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Take a Picture'),
      ),
      body: Container(),
      floatingActionButton: FloatingActionButton(
        onPressed: () async {
          await Navigator.push<void>(
            context,
            MaterialPageRoute(
              builder: (BuildContext context) {
                return PlatformCameraPage();
              },
            ),
          );
        },
        child: const Icon(Icons.camera_alt),
      ),
    );
  }
}
