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

    private val currentNetworkInfo= MutableLiveData<NetworkInfo>().apply {
        val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        value = NetworkInfo.from(capabilities)
    }
    private val networkInfoHandler: NetworkInfoHandler =
        NetworkInfoHandler()

    init {
        val wifiNetworkRequest = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .build()

        val ethernetNetworkRequest = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_ETHERNET)
            .build()

        val cellularNetworkRequest = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .build()

        connectivityManager.registerNetworkCallback(wifiNetworkRequest, NetworkCallback(NetworkCapabilities.TRANSPORT_WIFI))
        connectivityManager.registerNetworkCallback(ethernetNetworkRequest, NetworkCallback(NetworkCapabilities.TRANSPORT_ETHERNET))
        connectivityManager.registerNetworkCallback(cellularNetworkRequest, NetworkCallback(NetworkCapabilities.TRANSPORT_CELLULAR))
    }

    override fun getNetworkInfo(): LiveData<NetworkInfo> = currentNetworkInfo

    private inner class NetworkCallback(val transport: Int) : ConnectivityManager.NetworkCallback() {

        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            Log.i(TAG,"$transport::onAvailable")

            val networkInfo: NetworkInfo? = when (transport) {
                NetworkCapabilities.TRANSPORT_WIFI -> NetworkInfo(NetworkInfo.State.CONNECTED, NetworkInfo.Type.WIFI)
                NetworkCapabilities.TRANSPORT_ETHERNET -> NetworkInfo(NetworkInfo.State.CONNECTED, NetworkInfo.Type.ETHERNET)
                NetworkCapabilities.TRANSPORT_CELLULAR -> NetworkInfo(NetworkInfo.State.CONNECTED, NetworkInfo.Type.MOBILE)
                else -> null
            }

            networkInfo?.let { onNetworkInfoChange(it) }
        }

        override fun onLost(network: Network) {
            super.onLost(network)
            Log.i(TAG,"$transport::onLost")

            val networkInfo: NetworkInfo? = when (transport) {
                NetworkCapabilities.TRANSPORT_WIFI -> NetworkInfo(NetworkInfo.State.DISCONNECTED, NetworkInfo.Type.WIFI)
                NetworkCapabilities.TRANSPORT_ETHERNET -> NetworkInfo(NetworkInfo.State.DISCONNECTED, NetworkInfo.Type.ETHERNET)
                NetworkCapabilities.TRANSPORT_CELLULAR -> NetworkInfo(NetworkInfo.State.DISCONNECTED, NetworkInfo.Type.MOBILE)
                else -> null
            }

            networkInfo?.let { onNetworkInfoChange(it) }
        }
    }

    private fun onNetworkInfoChange(networkInfo: NetworkInfo) {
        Log.i(TAG,"Network update - $networkInfo")

        networkInfoHandler.execute(currentNetworkInfo.value, networkInfo) { info, updated ->
            Log.i(TAG,"Network update - $info, updated=$updated")
            this.currentNetworkInfo.postValue(info)
        }
    }
}