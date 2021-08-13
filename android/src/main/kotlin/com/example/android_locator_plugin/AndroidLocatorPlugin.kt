package com.example.android_locator_plugin

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.annotation.NonNull
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationRequest.create
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry


/** AndroidLocatorPlugin */
class AndroidLocatorPlugin : FlutterPlugin, MethodCallHandler, ActivityAware,
    PluginRegistry.ActivityResultListener {

    private lateinit var channel: MethodChannel
    private lateinit var activity: Activity
    private lateinit var context: Context
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback

    private lateinit var accessEventChannel: EventChannel
    private lateinit var accessEventSource: EventChannel.EventSink
    private var accessStreamHandler: EventChannel.StreamHandler =
        object : EventChannel.StreamHandler {
            override fun onListen(arguments: Any?, events: EventChannel.EventSink) {
                accessEventSource = events
            }

            override fun onCancel(arguments: Any?) {
            }
        }

    private lateinit var locationEventChannel: EventChannel
    private lateinit var locationEventSource: EventChannel.EventSink
    private var locationStreamHandler: EventChannel.StreamHandler =
        object : EventChannel.StreamHandler {
            override fun onListen(arguments: Any?, events: EventChannel.EventSink) {
                locationEventSource = events
            }

            override fun onCancel(arguments: Any?) {
            }
        }

    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        context = flutterPluginBinding.applicationContext
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, ANDROID_LOCATOR_PLUGIN)
        channel.setMethodCallHandler(this)
        accessEventChannel = EventChannel(flutterPluginBinding.binaryMessenger, ACCESS_STREAM)
        accessEventChannel.setStreamHandler(accessStreamHandler)
        locationEventChannel = EventChannel(flutterPluginBinding.binaryMessenger, EVENT_LOCATION)
        locationEventChannel.setStreamHandler(locationStreamHandler)
    }

    override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
        when (call.method) {
            CHECK_PERMISSION -> checkPermission(result)
            REQUEST_PERMISSION -> requestPermission(result)
            INITIALIZE -> initializePlugin(result)
            START_LOCALIZING -> startLocalizing(result)
            STOP_LOCALIZING -> stopLocalizing(result)
            else -> result.notImplemented()
        }
    }

    private fun requestPermission(result: Result) {
        context.run {
            activity.apply {
                requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ),
                    REQUEST_CODE
                )
            }
        }
        result.success(null)
    }

    private fun checkPermission(result: Result) {
        val granted = permissionGranted()
        accessEventSource.success(if (granted) ACCESS_GRANTED else ACCESS_DENIED)
        result.success(granted)
    }

    private fun permissionGranted(): Boolean {
        var granted: Boolean
        context.run {
            granted = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        }
        return granted
    }

    private fun initializePlugin(result: Result) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)
        val permission = permissionGranted()
        accessEventSource.success(if (permission) PLUGIN_INITIALIZED else PLUGIN_NEED_PERMISSION)
        if (permission) {
            locationRequest = create().apply {
                interval = LOCATION_REQUEST_INTERVAL
                priority = PRIORITY_HIGH_ACCURACY
                locationCallback = object : LocationCallback() {
                    override fun onLocationResult(locationResult: LocationResult?) {
                        locationEventSource.success(
                            locationResult?.convertToString()
                        )
                    }
                }
            }
        } else {
            locationEventSource.success(ACCESS_DENIED)
        }
        result.success(permission)
    }

    private fun startLocalizing(result: Result) {
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            null
        )
        result.success(true)
    }

    private fun stopLocalizing(result: Result) {
        context.run {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
        locationEventSource.success(FINISH_LOCALIZING)
        result.success(true)
    }

    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        activity = binding.activity
        binding.addActivityResultListener(this)
    }

    override fun onDetachedFromActivityForConfigChanges() {
        Log.i("onDetachedFromActivity", "No longer valid")
    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
        Log.i("onReattachedToActivity", "No longer valid")
    }

    override fun onDetachedFromActivity() {
        Log.i("onDetachedFromActivity", "No longer valid")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
        Log.i("onActivityResult", "No longer valid")
        return true
    }

    companion object {
        const val REQUEST_CODE = 500
        const val ACCESS_GRANTED = "Your access is granted!"
        const val ACCESS_DENIED = "Your access is not granted"
        const val CHECK_PERMISSION = "checkPermission"
        const val ACCESS_STREAM = "access_event_channel"
        const val EVENT_LOCATION = "access_location_channel"
        const val ANDROID_LOCATOR_PLUGIN = "android_locator_plugin"
        const val REQUEST_PERMISSION = "requestPermission"
        const val INITIALIZE = "initialize"
        const val PLUGIN_INITIALIZED = "The plugin has been initialized"
        const val PLUGIN_NEED_PERMISSION = "You need access first"
        const val LOCATION_REQUEST_INTERVAL = 1500L
        const val LONGITUDE = "Longitude: "
        const val LATITUDE = " -  Latitude: "
        const val START_LOCALIZING = "startLocalizing"
        const val STOP_LOCALIZING = "stopLocalizing"
        const val FINISH_LOCALIZING = "Find user's location has been stopped"
    }
}
