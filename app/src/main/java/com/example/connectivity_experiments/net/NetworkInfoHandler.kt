package com.example.connectivity_experiments.net

class NetworkInfoHandler {

    fun execute(currentNetworkInfo: NetworkInfo?, newNetworkInfo: NetworkInfo, output: (NetworkInfo, Boolean) -> Unit) {

        // sanity check
        if (currentNetworkInfo == null) {
            output(newNetworkInfo, true)
            return
        }

        val currentState = currentNetworkInfo.state
        val currentType = currentNetworkInfo.type

        val newState = newNetworkInfo.state
        val newType = newNetworkInfo.type

        var updatedState: NetworkInfo = currentNetworkInfo
        var updated = false


        // WIFI ON -> WIFI OFF

        if (currentType == NetworkInfo.Type.WIFI && currentState == NetworkInfo.State.CONNECTED) {

            if ((newType == NetworkInfo.Type.MOBILE) && newState == NetworkInfo.State.CONNECTED) {
                updatedState = newNetworkInfo
                updated = true
            }

            if ((newType == NetworkInfo.Type.ETHERNET) && newState == NetworkInfo.State.CONNECTED) {
                updatedState = newNetworkInfo
                updated = true
            }

            if ((newType == NetworkInfo.Type.WIFI) && newState == NetworkInfo.State.DISCONNECTED) {
                updatedState = newNetworkInfo
                updated = true
            }

            if ((newType == NetworkInfo.Type.NONE) && newState == NetworkInfo.State.DISCONNECTED) {
                updatedState = newNetworkInfo
                updated = true
            }
        }

        // ETHERNET ON -> ETHERNET OFF

        if (currentType == NetworkInfo.Type.ETHERNET && currentState == NetworkInfo.State.CONNECTED) {

            if ((newType == NetworkInfo.Type.MOBILE) && newState == NetworkInfo.State.CONNECTED) {
                updatedState = newNetworkInfo
                updated = true
            }

            if ((newType == NetworkInfo.Type.WIFI) && newState == NetworkInfo.State.CONNECTED) {
                updatedState = newNetworkInfo
                updated = true
            }

            if ((newType == NetworkInfo.Type.ETHERNET) && newState == NetworkInfo.State.DISCONNECTED) {
                updatedState = newNetworkInfo
                updated = true
            }

            if ((newType == NetworkInfo.Type.NONE) && newState == NetworkInfo.State.DISCONNECTED) {
                updatedState = newNetworkInfo
                updated = true
            }
        }

        // MOBILE ON -> MOBILE OFF

        if (currentType == NetworkInfo.Type.MOBILE && currentState == NetworkInfo.State.CONNECTED) {

            if ((newType == NetworkInfo.Type.WIFI) && newState == NetworkInfo.State.CONNECTED) {
                updatedState = newNetworkInfo
                updated = true
            }

            if ((newType == NetworkInfo.Type.ETHERNET) && newState == NetworkInfo.State.CONNECTED) {
                updatedState = newNetworkInfo
                updated = true
            }

            if ((newType == NetworkInfo.Type.MOBILE) && newState == NetworkInfo.State.DISCONNECTED) {
                updatedState = newNetworkInfo
                updated = true
            }

            if ((newType == NetworkInfo.Type.NONE) && newState == NetworkInfo.State.DISCONNECTED) {
                updatedState = newNetworkInfo
                updated = true
            }
        }

        // CONNECTION OFF -> CONNECTION ON

        if (currentState == NetworkInfo.State.DISCONNECTED) {

            if ((newType == NetworkInfo.Type.WIFI) && newState == NetworkInfo.State.CONNECTED) {
                updatedState = newNetworkInfo
                updated = true
            }

            if ((newType == NetworkInfo.Type.ETHERNET) && newState == NetworkInfo.State.CONNECTED) {
                updatedState = newNetworkInfo
                updated = true
            }

            if (newType == NetworkInfo.Type.MOBILE && newState == NetworkInfo.State.CONNECTED) {
                updatedState = newNetworkInfo
                updated = true
            }
        }

        // INITIAL STATE -> ANY

        if (currentState == NetworkInfo.State.UNKNOWN) {

            if (newState != NetworkInfo.State.UNKNOWN) {
                updatedState = newNetworkInfo
                updated = true
            }
        }

        output(updatedState, updated)
    }
}