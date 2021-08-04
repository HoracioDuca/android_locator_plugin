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
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY

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
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var context: Context
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback

    private lateinit var locationEventChannel: EventChannel
    private var locationEventSource: EventChannel.EventSink? = null
    private var locationStreamHandler: EventChannel.StreamHandler =
        object : EventChannel.StreamHandler {
            override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
                locationEventSource = events
            }

            override fun onCancel(arguments: Any?) {
                locationEventSource = null
            }
        }

    private lateinit var accessEventChannel: EventChannel
    private var accessEventSource: EventChannel.EventSink? = null
    private var accessStreamHandler: EventChannel.StreamHandler =
        object : EventChannel.StreamHandler {
            override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
                accessEventSource = events
            }

            override fun onCancel(arguments: Any?) {
                accessEventSource = null
            }
        }

    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        context = flutterPluginBinding.applicationContext
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, androidLocatorPlugin)
        channel.setMethodCallHandler(this)
        accessEventChannel = EventChannel(flutterPluginBinding.binaryMessenger, accessStream)
        accessEventChannel.setStreamHandler(accessStreamHandler)
        locationEventChannel = EventChannel(flutterPluginBinding.binaryMessenger, eventLocation)
        locationEventChannel.setStreamHandler(locationStreamHandler)
    }

    override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
        when (call.method) {
            initialize -> initializePlugin(result)
            checkPermission -> checkPermission(result)
            requestPermission -> requestPermission(result)
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
                    requestCode
                )
            }
        }
        result.success(null)
    }

    private fun checkPermission(result: Result) {
        val granted = permissionGranted()
        accessEventSource?.success(if (granted) accessGranted else accessDenied)
        result.success(granted)
    }

    private fun initializePlugin(result: Result) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)
        val permission = permissionGranted()
        accessEventSource?.success(if (permission) pluginInitialized else pluginNeedPermission)
        if (permission) {
            locationRequest = LocationRequest.create()
            locationRequest.interval = locationRequestInterval
            locationRequest.priority = PRIORITY_HIGH_ACCURACY
            locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult?) {
                    locationEventSource?.success(geographicLongitude + locationResult?.lastLocation?.longitude.toString() + " - " + geographicLatitude + locationResult?.lastLocation?.latitude.toString())
                }
            }
        } else {
            locationEventSource?.success(accessDenied)
        }
        result.success(permission)
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

    companion object PluginConstants {
        const val initialize: String = "initialize"
        const val locationRequestInterval: Long = 1500
        const val requestCode: Int = 500
        const val geographicLongitude: String = "Longitude: "
        const val geographicLatitude: String = "Latitude: "
        const val accessGranted: String = "Your access is granted!"
        const val accessDenied: String = "Your access is not granted"
        const val checkPermission: String = "checkPermission"
        const val accessStream: String = "access_event_channel"
        const val eventLocation: String = "location_event_channel"
        const val androidLocatorPlugin: String = "android_locator_plugin"
        const val requestPermission: String = "requestPermission"
        const val pluginInitialized: String = "The plugin has been initialized"
        const val pluginNeedPermission: String = "You need access first"
    }

}
