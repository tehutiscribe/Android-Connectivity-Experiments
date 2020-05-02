package com.example.connectivity_experiments.net

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.util.Log
import androidx.lifecycle.MutableLiveData

class LegacyConnectivityMonitor(context: Context) : IConnectivityMonitor {

    private val TAG = "LegacyConnectivityMonitor"

    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private val receiver = ConnectivityReceiver()
    private val currentNetworkInfo= MutableLiveData<NetworkInfo>().apply { value = NetworkInfo.from(connectivityManager.activeNetworkInfo) }
    private val networkInfoHandler = NetworkInfoHandler()

    init {
        context.registerReceiver(receiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
    }

    override fun getNetworkInfo() = currentNetworkInfo

    private inner class ConnectivityReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            val networkInfo = NetworkInfo.from(readNetworkInfo(intent))
            onNetworkInfoUpdate(networkInfo)
        }
    }

    @SuppressLint("LongLogTag")
    private fun onNetworkInfoUpdate(networkInfo: NetworkInfo) {
        Log.i(TAG, "Network update - $networkInfo")

        networkInfoHandler.execute(getNetworkInfo().value, networkInfo) { info: NetworkInfo, updated: Boolean ->
            Log.i(TAG, "Network update - $info, updated=$updated")
            this.currentNetworkInfo.value = info
        }
    }

    private fun readNetworkInfo(intent: Intent?): android.net.NetworkInfo? =
        intent?.extras?.get(ConnectivityManager.EXTRA_NETWORK_INFO) as android.net.NetworkInfo?
}