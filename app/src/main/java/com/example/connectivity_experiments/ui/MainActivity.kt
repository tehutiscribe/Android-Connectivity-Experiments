package com.example.connectivity_experiments.ui

import android.os.Build
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.connectivity_experiments.R
import com.example.connectivity_experiments.net.ConnectivityMonitor
import com.example.connectivity_experiments.net.LegacyConnectivityMonitor

class MainActivity : AppCompatActivity() {

    private lateinit var connectivityManagerTextView: TextView
    private lateinit var networkCallbacksTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        connectivityManagerTextView = findViewById(R.id.text_connectivity_manager)
        networkCallbacksTextView = findViewById(R.id.text_network_callbacks)

        setupLegacyConnectivityMonitor()
        setupConnectivityMonitor()
    }

    private fun setupLegacyConnectivityMonitor() {
        val legacyConnectivityMonitor = LegacyConnectivityMonitor(this)
        legacyConnectivityMonitor.getNetworkInfo().observe(this, Observer {
            connectivityManagerTextView.text = "ConnectivityManager\n$it"
        })
    }

    private fun setupConnectivityMonitor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val connectivityMonitor = ConnectivityMonitor(this)
            connectivityMonitor.getNetworkInfo().observe(this, Observer {
                networkCallbacksTextView.text = "NetworkCallbacks\n$it"
            })
        }
    }
}
