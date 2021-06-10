import 'dart:async';
import 'dart:io';

// ignore: import_of_legacy_library_into_null_safe
import 'package:camera/camera.dart';
import 'package:flutter/material.dart';
import 'package:flutter_camera_sample/platform_camera_page.dart';
import 'package:path/path.dart';
import 'package:path_provider/path_provider.dart';

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
  late CameraController _controller;
  late Future<void> _initializeCamera;

  @override
  void initState() {
    super.initState();

    _initCameraController();
  }

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Take a Picture'),
      ),
      body: _buildCameraPreview(),
      floatingActionButton: FloatingActionButton(
        onPressed: () {
          takePicture(context);
        },
        child: const Icon(Icons.camera_alt),
      ),
    );
  }

  Future<void> _initCameraController() async {
    final cameras = await availableCameras();

    if (cameras.isNotEmpty) {
      _controller = CameraController(cameras.first, ResolutionPreset.max);
      _initializeCamera = _controller.initialize();
    }
  }

  Widget _buildCameraPreview() {
    return FutureBuilder<void>(
      future: _initializeCamera,
      builder: (BuildContext context, AsyncSnapshot<void> snapshot) {
        return snapshot.connectionState == ConnectionState.done
            ? CameraPreview(_controller)
            : CircularProgressIndicator();
      },
    );
  }

  Future<void> takePicture(BuildContext context) async {
    try {
      await _initializeCamera;

      final imagePath = join(
        (await getApplicationDocumentsDirectory()).path,
        '${DateTime.now()}.png',
      );

      await _controller.takePicture(imagePath);
      print('imagePath : $imagePath');
      await Navigator.of(context).push<void>(
        MaterialPageRoute(
          builder: (BuildContext context) => PlatformCameraPage(),
        ),
      );
    } catch (e) {
      print(e);
    }
  }
}

class DisplayPictureScree extends StatelessWidget {
  final String imagePath;

  DisplayPictureScree({
    Key? key,
    required this.imagePath,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Display Picture'),
      ),
      body: Image.file(
        File(imagePath),
      ),
    );
  }
}

class MyApp extends StatelessWidget {
  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter Demo',
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      home: MyHomePage(title: 'Flutter Demo Home Page'),
    );
  }
}

class MyHomePage extends StatefulWidget {
  MyHomePage({Key? key, required this.title}) : super(key: key);
  final String title;

  @override
  _MyHomePageState createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  int _counter = 0;

  void _incrementCounter() {
    setState(() {
      _counter++;
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(widget.title),
      ),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: <Widget>[
            Text(
              'You have pushed the button this many times:',
            ),
            Text(
              '$_counter',
              style: Theme.of(context).textTheme.headline4,
            ),
          ],
        ),
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: _incrementCounter,
        tooltip: 'Increment',
        child: Icon(Icons.add),
      ), // This trailing comma makes auto-formatting nicer for build methods.
    );
  }
}
