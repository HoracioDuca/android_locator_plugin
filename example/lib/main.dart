import 'utils/example_strings.dart';
import 'widgets/plugin_button.dart';
import 'package:flutter/material.dart';
import 'dart:async';
import 'package:flutter/services.dart';
import 'package:android_locator_plugin/android_locator_plugin.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _platformVersion = ExampleStrings.platformVersion;

  @override
  void initState() {
    super.initState();
    //initPlatformState();
  }

  Future<void> initPlatformState() async {
    String platformVersion;

    try {
      platformVersion = await AndroidLocatorPlugin.platformVersion ??
          ExampleStrings.unknownPlatformVersion;
    } on PlatformException {
      platformVersion = ExampleStrings.platformFail;
    }

    if (!mounted) return;

    setState(() {
      _platformVersion = platformVersion;
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      home: Scaffold(
        appBar: AppBar(
          title: const Text(
            ExampleStrings.appBarTitle,
          ),
        ),
        body: Center(
          child: Column(
            mainAxisAlignment: MainAxisAlignment.spaceEvenly,
            children: [
              Column(
                children: [
                  PluginButton(
                    text: ExampleStrings.checkPermission,
                    onPressed: () {
                      AndroidLocatorPlugin.checkPermission;
                    },
                  ),
                  StreamBuilder(
                    stream: AndroidLocatorPlugin.accessStream,
                    builder: (
                      BuildContext context,
                      AsyncSnapshot<dynamic> snapshot,
                    ) {
                      return snapshot.hasData
                          ? Text(
                              snapshot.data.toString(),
                            )
                          : Text(
                              ExampleStrings.permissionInfo,
                            );
                    },
                  ),
                ],
              ),
              Column(
                children: [
                  PluginButton(
                    text: ExampleStrings.requestPermission,
                    onPressed: () {
                      AndroidLocatorPlugin.requestPermission;
                    },
                  ),
                ],
              ),
              Column(
                children: [
                  PluginButton(
                    text: ExampleStrings.initializePlugin,
                    onPressed: () {
                      AndroidLocatorPlugin.initializePlugin;
                    },
                  ),
                ],
              ),
            ],
          ),
        ),
      ),
    );
  }
}
