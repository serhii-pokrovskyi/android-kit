/*
 * Developed by Serhii Pokrovskyi
 * e-mail: pokrovskyi.dev@gmail.com
 * Last modified: 9/28/19 12:52 PM
 * Copyright (c) 2019
 * All rights reserved
 */

package one.brainyapps.androidkit.livedata

import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Just a single call.
 * WARNING: Make sure that you have only one observer - because only one will be called.
 * And there is no way to know witch one will.
 */
class SingleCall : LiveData<Unit>() {

    private val mPending = AtomicBoolean(false)

    override fun observe(owner: LifecycleOwner, observer: Observer<in Unit>) {
        super.observe(owner, Observer { v ->
            if (mPending.compareAndSet(true, false)) {
                observer.onChanged(v)
            }
        })
    }

    @MainThread
    override fun setValue(value: Unit?) {
        mPending.set(true)
        super.setValue(value)
    }

    /**
     * Makes a single call to observer/
     * Use only from main thread
     */
    @MainThread
    fun call() {
        value = null
    }

    /**
     * Makes a single call to observer/
     * Can be used from main and background threads
     */
    @WorkerThread
    fun postCall() {
        postValue(null)
    }
}