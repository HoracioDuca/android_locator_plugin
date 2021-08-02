
import 'dart:async';

import 'package:flutter/services.dart';

class AndroidLocatorPlugin {
  static const MethodChannel _channel =
      const MethodChannel('android_locator_plugin');

  static Future<String?> get platformVersion async {
    final String? version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }
}
