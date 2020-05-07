package com.example.connectivity_experiments.net.handler.impl

import android.util.Log
import com.example.connectivity_experiments.net.IConnectivityMonitor
import com.example.connectivity_experiments.net.NetworkInfo
import com.example.connectivity_experiments.net.handler.base.NetworkInfoHandlerBaseImpl
import java.util.concurrent.*

class DebounceNetworkInfoHandler(monitor: IConnectivityMonitor) :
    NetworkInfoHandlerBaseImpl(monitor) {

    private val executor: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
    private val debounceMap = ConcurrentHashMap<String, Future<*>>()

    override fun doWork(networkInfo: NetworkInfo, callback: (updatedInfo: NetworkInfo) -> Unit) {

        val eventKey = "${networkInfo.type}-${networkInfo.state}"
        Log.i(TAG, "Debounce:: Scheduling event=$networkInfo with key=$eventKey")

        val task = executor.schedule({
            Log.i(TAG, "Debounce:: executing event=$networkInfo with key=$eventKey")
            callback(networkInfo)
            debounceMap.remove(eventKey)
        }, DEBOUNCE_DELAY, TimeUnit.MILLISECONDS)

        // replace network update
        debounceMap.put(eventKey, task)?.let {
            try {
                Log.i(TAG, "Debounce:: cancelling previous event with key=$eventKey")
                it.cancel(true)
            } catch (e: CancellationException) {
                Log.i(TAG,"Debounce:: Exception thrown cancelling previous event with key=$eventKey error=${e.localizedMessage}")
            }
        }
    }

    companion object {
        private const val DEBOUNCE_DELAY = 2000L
    }
}