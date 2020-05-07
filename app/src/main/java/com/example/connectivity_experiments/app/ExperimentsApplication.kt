package com.example.connectivity_experiments.app

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.connectivity_experiments.net.ConnectivityMonitor
import com.example.connectivity_experiments.net.LegacyConnectivityMonitor

class ExperimentsApplication : Application() {

    lateinit var appStateMonitor: AppStateMonitor

    lateinit var legacyConnectivityMonitor: LegacyConnectivityMonitor
    lateinit var connectivityMonitor: ConnectivityMonitor
        @RequiresApi(Build.VERSION_CODES.M)
        get

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this

        appStateMonitor = AppStateMonitor(this)
        legacyConnectivityMonitor = LegacyConnectivityMonitor(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            connectivityMonitor = ConnectivityMonitor(this)
        }
    }

    companion object {
        lateinit var INSTANCE: ExperimentsApplication
    }
}