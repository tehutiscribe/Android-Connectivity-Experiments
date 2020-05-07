package com.example.connectivity_experiments.app

import android.app.Activity
import android.app.Application
import android.content.ComponentCallbacks2
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log

class AppStateMonitor(application: Application) : Application.ActivityLifecycleCallbacks, ComponentCallbacks2 {

    interface Listener {
        fun onAppSateChange(appState: AppState)
    }

    enum class AppState {
        FOREGROUND,
        BACKGROUND,
    }

    var appState =
        AppState.FOREGROUND
        private set

    private val listeners = mutableSetOf<Listener>()

    init {
        application.registerActivityLifecycleCallbacks(this)
        application.registerComponentCallbacks(this)
    }

    fun addListener(listener: Listener) {
        listeners.add(listener)
        listener.onAppSateChange(appState)
    }

    fun removeListener(listener: Listener) {
        listeners.remove(listener)
    }

    override fun onActivityPaused(activity: Activity) { }

    override fun onActivityStarted(activity: Activity) {
        if (appState != AppState.FOREGROUND) {
            onStateChange(AppState.FOREGROUND)
        }
    }

    override fun onActivityDestroyed(activity: Activity) { }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) { }

    override fun onActivityStopped(activity: Activity) { }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) { }

    override fun onActivityResumed(activity: Activity) { }

    override fun onLowMemory() { }

    override fun onConfigurationChanged(newConfig: Configuration) { }

    override fun onTrimMemory(level: Int) {
        if (level >= ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN) {
            onStateChange(AppState.BACKGROUND)
        }
    }

    private fun onStateChange(appState: AppState) {
        Log.i(TAG,"State changed ${this.appState} -> $appState")
        this.appState = appState

        for (listener in listeners) {
            listener.onAppSateChange(appState)
        }
    }

    companion object {
        private const val TAG = "AppStateMonitor"
    }
}