package com.example.connectivity_experiments.net.handler.impl

import android.util.Log
import com.example.connectivity_experiments.app.AppStateMonitor
import com.example.connectivity_experiments.app.AppStateMonitor.AppState.BACKGROUND
import com.example.connectivity_experiments.app.AppStateMonitor.AppState.FOREGROUND
import com.example.connectivity_experiments.app.ExperimentsApplication
import com.example.connectivity_experiments.net.IConnectivityMonitor
import com.example.connectivity_experiments.net.NetworkInfo
import com.example.connectivity_experiments.net.handler.base.NetworkInfoHandlerBaseImpl
import java.util.*

class AppStateHandler(monitor: IConnectivityMonitor) : NetworkInfoHandlerBaseImpl(monitor),
    AppStateMonitor.Listener {

    private val pendingEvents = LinkedList<NetworkUpdate>()

    private val appStateMonitor: AppStateMonitor =
        ExperimentsApplication.INSTANCE.appStateMonitor.also { it.addListener(this) }

    override fun doWork(networkInfo: NetworkInfo, callback: (updatedInfo: NetworkInfo) -> Unit) {

        Log.i(TAG, "AppState:: Handling event ...")

        when (appStateMonitor.appState) {
            FOREGROUND -> {
                Log.i(TAG, "AppState:: app in foreground, handling complete")
                callback(networkInfo)
            }
            BACKGROUND -> {
                Log.i(TAG, "AppState:: app in background, saving update = $networkInfo")
                saveUpdate(networkInfo, callback)
            }
        }
    }

    private fun saveUpdate(networkInfo: NetworkInfo, callback: (updatedInfo: NetworkInfo) -> Unit) {
        if (pendingEvents.size == QUEUE_SIZE) {
            pendingEvents.removeFirst()
        }
        pendingEvents.add(NetworkUpdate(networkInfo, callback))
    }

    private fun executePendingEvents() {
        pendingEvents.iterator().run {
            while (hasNext()) {
                val networkUpdate = next()
                Log.i(TAG, "AppState:: app foreground, handling saved update = ${networkUpdate.networkInfo}")
                networkUpdate.execute()
                remove()
            }
        }
    }

    override fun onAppSateChange(appState: AppStateMonitor.AppState) {
        if (appState == FOREGROUND) executePendingEvents()
    }

    companion object {
        private const val QUEUE_SIZE = 2

        data class NetworkUpdate(
            val networkInfo: NetworkInfo,
            val callback: (updatedInfo: NetworkInfo) -> Unit
        ) {
            fun execute() {
                callback(networkInfo)
            }
        }
    }
}