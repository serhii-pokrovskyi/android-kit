/*
 * Developed by Serhii Pokrovskyi
 * e-mail: pokrovskyi.dev@gmail.com
 * Last modified: 10/2/19 10:13 PM
 * Copyright (c) 2019
 * All rights reserved
 */

package one.brainyapps.androidkit.logger

import android.util.Log

abstract class Galaxy {

    //todo: readme
    open val tag: String? = this.javaClass.simpleName

    fun i(msg: String) {
        log(Log.INFO, msg, null)
    }

    fun i(msg: String, throwable: Throwable) {
        log(Log.INFO, msg, throwable)
    }

    open fun log(priority: Int, msg: String?, throwable: Throwable?) {
        if (msg.isNullOrBlank()) {
            return
        }

        throwable?.let {
            Log.i(tag, msg, it)
        } ?: Log.i(tag, msg)
    }
}
