import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:android_locator_plugin/android_locator_plugin.dart';

void main() {
  const MethodChannel channel = MethodChannel('android_locator_plugin');

  TestWidgetsFlutterBinding.ensureInitialized();

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('getPlatformVersion', () async {
    expect(await AndroidLocatorPlugin.platformVersion, '42');
  });
}
