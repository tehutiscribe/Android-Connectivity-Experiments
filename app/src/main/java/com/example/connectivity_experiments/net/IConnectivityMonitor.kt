package com.example.connectivity_experiments.net

import androidx.lifecycle.LiveData

interface IConnectivityMonitor {

    fun getNetworkInfo(): LiveData<NetworkInfo>
}