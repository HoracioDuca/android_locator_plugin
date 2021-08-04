import 'dart:async';

import 'package:flutter/services.dart';

class AndroidLocatorPlugin {
  static const MethodChannel _channel =
      const MethodChannel('android_locator_plugin');

  static Future<String?> get platformVersion async {
    final String? version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  static Future<void> get initializePlugin async {
    await _channel.invokeMethod('initialize');
    return;
  }

  static Future<bool> get checkPermission async {
    final bool granted = await _channel.invokeMethod('checkPermission');
    return granted;
  }

  static const _accessStream = EventChannel('access_event_channel');
  static Stream<dynamic> accessStream = _accessStream.receiveBroadcastStream();

  static Future<void> get requestPermission async {
    await _channel.invokeMethod('requestPermission');
    return;
  }
}
