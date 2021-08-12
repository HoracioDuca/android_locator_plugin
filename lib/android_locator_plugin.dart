import 'dart:async';
import 'utils/plugin_strings.dart';
import 'package:flutter/services.dart';

class AndroidLocatorPlugin {
  static const MethodChannel _channel =
      const MethodChannel(PluginStrings.methodChannel);

  static const _accessStream = EventChannel(PluginStrings.eventChannel);
  static Stream<dynamic> accessStream = _accessStream.receiveBroadcastStream();

  static const _locationStream = EventChannel(PluginStrings.locationChannel);
  static Stream<dynamic> locationStream =
      _locationStream.receiveBroadcastStream();

  static Future<bool> get checkPermission async {
    final bool granted =
        await _channel.invokeMethod(PluginStrings.checkPermission);
    return granted;
  }

  static Future<void> get requestPermission async {
    await _channel.invokeMethod(PluginStrings.requestPermission);
    return;
  }

  static Future<void> get initializePlugin async {
    await _channel.invokeMethod(PluginStrings.initialize);
    return;
  }

  static Future<bool> get startLocalizing async {
    final bool initialize =
        await _channel.invokeMethod(PluginStrings.startLocalizing);
    return initialize;
  }

  static Future<bool> get stopLocalizing async {
    final bool finalize =
        await _channel.invokeMethod(PluginStrings.stopLocalizing);
    return finalize;
  }
}
