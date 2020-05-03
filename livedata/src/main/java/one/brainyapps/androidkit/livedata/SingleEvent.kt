/*
 * Developed by Serhii Pokrovskyi
 * e-mail: serg.pokrovskyi@gmail.com
 * Last modified: 5/3/20 9:25 PM
 * Copyright (c) 2020
 * All rights reserved
 */

package one.brainyapps.androidkit.livedata

import androidx.annotation.MainThread
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Just a single event.
 * WARNING: Make sure that you have only one observer - because only one will be called.
 * And there is no way to know witch one will.
 *
 * @param[T] - type that you wold like to pass to observer.
 *
 * You can pass *null* if you want just trigger [Observer.onChanged], however, for such cases you
 * better use [SingleCall]
 */
class SingleEvent<T> : MutableLiveData<T>() {

    private val mPending = AtomicBoolean(false)

    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        super.observe(owner, Observer { t ->
            if (mPending.compareAndSet(true, false)) {
                observer.onChanged(t)
            }
        })
    }

    @MainThread
    override fun setValue(t: T?) {
        mPending.set(true)
        super.setValue(t)
    }
}