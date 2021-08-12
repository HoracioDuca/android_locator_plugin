package com.example.android_locator_plugin

import com.google.android.gms.location.LocationResult
import kotlin.math.pow
import kotlin.math.round

fun LocationResult.convertToString(): String {
    return StringBuilder().append(AndroidLocatorPlugin.LONGITUDE)
        .append(Constants.WHITE_SPACE)
        .append(this.lastLocation?.longitude?.run {
            this.cutToDecimals(Constants.DECIMALS_NUMBER)
        })
        .append(Constants.WHITE_SPACE)
        .append(AndroidLocatorPlugin.LATITUDE)
        .append(Constants.WHITE_SPACE)
        .append(this.lastLocation?.latitude?.run {
            this.cutToDecimals(Constants.DECIMALS_NUMBER)
        }).toString()
}

fun Double.cutToDecimals(decimals: Int): Double {
    val numbOfDigits = Constants.BASE.toDouble().pow(decimals)
    return round(this * numbOfDigits) / numbOfDigits
}
