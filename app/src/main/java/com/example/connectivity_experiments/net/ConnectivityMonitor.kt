package com.example.connectivity_experiments.net

import android.content.Context
import android.net.*
import android.net.wifi.WifiManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.connectivity_experiments.net.handler.INetworkInfoHandler
import com.example.connectivity_experiments.net.handler.factory.NetworkInfoHandlerFactory

@RequiresApi(Build.VERSION_CODES.M)
class ConnectivityMonitor(context: Context) : IConnectivityMonitor {

    private val TAG = "ConnectivityMonitor"

    private val connectivityManager = context.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

    private val currentNetworkInfo= MutableLiveData<NetworkInfo>().apply {
        val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        value = NetworkInfo.from(capabilities, wifiManager.connectionInfo)
    }

    private val networkInfoHandler: INetworkInfoHandler = NetworkInfoHandlerFactory.create(this)

    init {
        // set wifi network callback
        val wifiNetworkRequest = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .build()
        connectivityManager.registerNetworkCallback(wifiNetworkRequest, NetworkCallback(NetworkCapabilities.TRANSPORT_WIFI))

        // set ethernet network callback
        val ethernetNetworkRequest = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_ETHERNET)
            .build()
        connectivityManager.registerNetworkCallback(ethernetNetworkRequest, NetworkCallback(NetworkCapabilities.TRANSPORT_ETHERNET))

        // set cellular network callback
        val cellularNetworkRequest = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .build()
        connectivityManager.registerNetworkCallback(cellularNetworkRequest, NetworkCallback(NetworkCapabilities.TRANSPORT_CELLULAR))
    }

    override fun getNetworkInfo(): LiveData<NetworkInfo> = currentNetworkInfo

    private inner class NetworkCallback(val transport: Int) : ConnectivityManager.NetworkCallback() {

        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            Log.i(TAG,"$transport::onAvailable")

            val networkInfo: NetworkInfo? = when (transport) {
                NetworkCapabilities.TRANSPORT_WIFI -> NetworkInfo(
                    NetworkInfo.State.CONNECTED,
                    NetworkInfo.Type.WIFI,
                    WifiInfo.from(wifiManager.connectionInfo)
                )
                NetworkCapabilities.TRANSPORT_ETHERNET -> NetworkInfo(
                    NetworkInfo.State.CONNECTED,
                    NetworkInfo.Type.ETHERNET,
                    WifiInfo.from(wifiManager.connectionInfo)
                )
                NetworkCapabilities.TRANSPORT_CELLULAR -> NetworkInfo(
                    NetworkInfo.State.CONNECTED,
                    NetworkInfo.Type.MOBILE
                )
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

        networkInfoHandler.handle(networkInfo) { updatedNetworkInfo: NetworkInfo ->
            Log.i(TAG,"Network update handled - $updatedNetworkInfo")
            currentNetworkInfo.postValue(updatedNetworkInfo)
       }
    }
}