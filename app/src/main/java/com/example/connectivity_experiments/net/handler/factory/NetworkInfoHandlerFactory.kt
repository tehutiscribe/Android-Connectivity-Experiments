package com.example.connectivity_experiments.net.handler.factory

import com.example.connectivity_experiments.net.ConnectivityMonitor
import com.example.connectivity_experiments.net.handler.INetworkInfoHandler
import com.example.connectivity_experiments.net.handler.impl.AppStateHandler
import com.example.connectivity_experiments.net.handler.impl.DebounceNetworkInfoHandler

class NetworkInfoHandlerFactory {

    companion object {
        fun create(connectivityMonitor: ConnectivityMonitor): INetworkInfoHandler {
            return AppStateHandler(connectivityMonitor).apply {
                addNext(DebounceNetworkInfoHandler(connectivityMonitor))
            }
        }
    }
}