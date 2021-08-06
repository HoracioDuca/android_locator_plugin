import 'utils/example_constants.dart';
import 'widgets/plugin_button.dart';
import 'package:flutter/material.dart';
import 'package:android_locator_plugin/android_locator_plugin.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  @override
  void initState() {
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      home: Scaffold(
        appBar: AppBar(
          title: const Text(
            ExampleConstants.appBarTitle,
          ),
        ),
        body: Center(
          child: Column(
            children: [
              PluginButton(
                text: ExampleConstants.checkPermission,
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
                          ExampleConstants.permissionInfo,
                        );
                },
              ),
              PluginButton(
                text: ExampleConstants.requestPermission,
                onPressed: () {
                  AndroidLocatorPlugin.requestPermission;
                },
              ),
            ],
          ),
        ),
      ),
    );
  }
}
