#import "AndroidLocatorPlugin.h"
#if __has_include(<android_locator_plugin/android_locator_plugin-Swift.h>)
#import <android_locator_plugin/android_locator_plugin-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "android_locator_plugin-Swift.h"
#endif

@implementation AndroidLocatorPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftAndroidLocatorPlugin registerWithRegistrar:registrar];
}
@end
