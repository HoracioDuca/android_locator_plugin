import 'dart:async';
import 'utils/plugin_strings.dart';
import 'package:flutter/services.dart';

class AndroidLocatorPlugin {
  static const MethodChannel _channel =
      const MethodChannel(PluginStrings.methodChannel);

  static Future<bool> get checkPermission async {
    final bool granted =
        await _channel.invokeMethod(PluginStrings.checkPermission);
    return granted;
  }

  static const _accessStream = EventChannel(PluginStrings.eventChannel);
  static Stream<dynamic> accessStream = _accessStream.receiveBroadcastStream();

  static Future<void> get requestPermission async {
    await _channel.invokeMethod(PluginStrings.requestPermission);
    return;
  }
}
