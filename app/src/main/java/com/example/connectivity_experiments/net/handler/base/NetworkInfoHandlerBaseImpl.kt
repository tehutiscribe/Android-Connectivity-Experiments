package com.example.connectivity_experiments.net.handler.base

import com.example.connectivity_experiments.net.IConnectivityMonitor
import com.example.connectivity_experiments.net.NetworkInfo
import com.example.connectivity_experiments.net.handler.INetworkInfoHandler

abstract class NetworkInfoHandlerBaseImpl(monitor: IConnectivityMonitor) : INetworkInfoHandler {

    protected val TAG = "NetworkInfoHandler"

    private var nextHandler: INetworkInfoHandler? = null

    override fun addNext(handler: INetworkInfoHandler) {
        when (nextHandler) {
            null -> nextHandler = handler
            else -> nextHandler?.addNext(handler)
        }
    }

    override fun handle(
        networkInfo: NetworkInfo,
        callback: (updatedNetworkInfo: NetworkInfo) -> Unit
    ) {
        doWork(networkInfo) { updatedInfo: NetworkInfo ->
            nextHandler?.handle(updatedInfo, callback) ?: callback(updatedInfo)
        }
    }

    protected abstract fun doWork(
        networkInfo: NetworkInfo,
        callback: (updatedInfo: NetworkInfo) -> Unit
    )
}