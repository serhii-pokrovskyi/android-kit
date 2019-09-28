/*
 * Developed by Serhii Pokrovskyi
 * e-mail: pokrovskyi.dev@gmail.com
 * Last modified: 9/28/19 11:44 AM
 * Copyright (c) 2019
 * All rights reserved
 */

package one.brainyapps.androidkit.livedata.util

import androidx.annotation.NonNull
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry

/**
 * Kind of emulates live cycle
 */
class TestLifecycleOwner : LifecycleOwner {

    private val registry = LifecycleRegistry(this)

    val state: Lifecycle.State
        @NonNull
        get() = registry.currentState

    fun onCreate(): TestLifecycleOwner {
        return handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
    }

    fun onStart(): TestLifecycleOwner {
        return handleLifecycleEvent(Lifecycle.Event.ON_START)
    }

    fun onResume(): TestLifecycleOwner {
        return handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
    }

    fun onPause(): TestLifecycleOwner {
        return handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    }

    fun onStop(): TestLifecycleOwner {
        return handleLifecycleEvent(Lifecycle.Event.ON_STOP)
    }

    fun onDestroy(): TestLifecycleOwner {
        return handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    }

    private fun handleLifecycleEvent(event: Lifecycle.Event): TestLifecycleOwner {
        registry.handleLifecycleEvent(event)
        return this
    }

    @NonNull
    override fun getLifecycle(): Lifecycle {
        return registry
    }
}