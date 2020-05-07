package com.example.connectivity_experiments.net.handler

import com.example.connectivity_experiments.net.NetworkInfo

interface INetworkInfoHandler {

    fun addNext(handler: INetworkInfoHandler)

    fun handle(networkInfo: NetworkInfo, callback: (updatedNetworkInfo: NetworkInfo) -> Unit)
}