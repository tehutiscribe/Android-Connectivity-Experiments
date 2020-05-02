package com.example.connectivity_experiments.net

import android.content.Context
import android.net.*
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

@RequiresApi(Build.VERSION_CODES.M)
class ConnectivityMonitor(context: Context) : IConnectivityMonitor {

    private val TAG = "ConnectivityMonitor"

    private val connectivityManager = context.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private val networkCallback: NetworkCallback = NetworkCallback()

    private val currentNetworkInfo = MutableLiveData<NetworkInfo>().apply {
        val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        value = NetworkInfo.from(capabilities)
    }
    private val networkInfoHandler: NetworkInfoHandler =
        NetworkInfoHandler()

    init {
        val networkRequest = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_ETHERNET)
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .build()

        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
    }

    override fun getNetworkInfo(): LiveData<NetworkInfo> = currentNetworkInfo

    private inner class NetworkCallback() : ConnectivityManager.NetworkCallback() {

        private val TAG = "NetworkCallback"

        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            Log.i(TAG, "onAvailable")
            onNetworkInfoChange(NetworkInfo.from(connectivityManager.getNetworkCapabilities(network)))
        }

        override fun onLost(network: Network) {
            super.onLost(network)
            Log.i(TAG, "onLost")
            onNetworkInfoChange(NetworkInfo.from(connectivityManager.getNetworkCapabilities(network)))
        }

        override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
            super.onCapabilitiesChanged(network, networkCapabilities)
            Log.i(TAG, "onCapabilitiesChanged")
        }

        override fun onLinkPropertiesChanged(network: Network, linkProperties: LinkProperties) {
            super.onLinkPropertiesChanged(network, linkProperties)
            Log.i(TAG, "onLinkPropertiesChanged")
        }

        override fun onUnavailable() {
            super.onUnavailable()
            Log.i(TAG, "onUnavailable")
        }

        override fun onLosing(network: Network, maxMsToLive: Int) {
            super.onLosing(network, maxMsToLive)
            Log.i(TAG, "onUnavailable")
        }
    }

    private fun onNetworkInfoChange(networkInfo: NetworkInfo) {
        Log.i(TAG, "Network update - $networkInfo")

        networkInfoHandler.execute(currentNetworkInfo.value, networkInfo) { info, updated ->
            Log.i(TAG, "Network update - $info, updated=$updated")
            this.currentNetworkInfo.postValue(info)
        }
    }
}