package com.example.connectivity_experiments.net

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.annotation.RequiresApi

data class NetworkInfo(
    var state: State,
    var type: Type
) {

    enum class State {
        CONNECTED,
        CONNECTING,
        SUSPENDED,
        DISCONNECTING,
        DISCONNECTED,
        UNKNOWN;

        companion object {
            @JvmStatic
            fun ofValue(state: android.net.NetworkInfo.State?): State = when (state) {
                android.net.NetworkInfo.State.CONNECTED -> CONNECTED
                android.net.NetworkInfo.State.CONNECTING -> CONNECTING
                android.net.NetworkInfo.State.SUSPENDED -> SUSPENDED
                android.net.NetworkInfo.State.DISCONNECTING -> DISCONNECTING
                android.net.NetworkInfo.State.DISCONNECTED -> DISCONNECTED
                android.net.NetworkInfo.State.UNKNOWN -> UNKNOWN
                null -> UNKNOWN
            }

            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            fun from(capabilities: NetworkCapabilities?): State {
                return when {
                    capabilities == null -> DISCONNECTED
                    capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) -> CONNECTED
                    else -> DISCONNECTED
                }
            }
        }
    }

    enum class Type {
        MOBILE,
        WIFI,
        ETHERNET,
        NONE;

        companion object {
            @JvmStatic
            fun ofValue(value: Int?): Type = when (value) {
                ConnectivityManager.TYPE_WIFI -> WIFI
                ConnectivityManager.TYPE_MOBILE -> MOBILE
                ConnectivityManager.TYPE_ETHERNET -> ETHERNET
                else -> NONE
            }

            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            fun from(capabilities: NetworkCapabilities?): Type = when {
                capabilities == null -> NONE
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> WIFI
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> ETHERNET
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> MOBILE
                else -> NONE
            }
        }
    }

    companion object Factory {
        @JvmStatic
        fun from(netInfo: android.net.NetworkInfo?) =
            NetworkInfo(State.ofValue(netInfo?.state), Type.ofValue(netInfo?.type))

        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        @JvmStatic
        fun from(capabilities: NetworkCapabilities?): NetworkInfo =
            NetworkInfo(State.from(capabilities), Type.from( capabilities))
    }
}
