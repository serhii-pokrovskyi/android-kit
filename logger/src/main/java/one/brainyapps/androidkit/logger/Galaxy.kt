/*
 * Developed by Serhii Pokrovskyi
 * e-mail: pokrovskyi.dev@gmail.com
 * Last modified: 9/30/19 3:44 PM
 * Copyright (c) 2019
 * All rights reserved
 */

package one.brainyapps.androidkit.logger

import android.util.Log

abstract class Galaxy {

    //todo: make setter and usage of this vars
    private var isVerboseOutput = true
    private var isDebugOutput = true
    private var isInfoOutput = true
    private var isWarnOutput = true
    private var isErrorOutput = true
    private var isAssertOutput = true

    fun i(msg: String) {
        if (!isInfoOutput) {
            return
        }
        log(Log.INFO, msg)
    }

    fun i(msg: String, throwable: Throwable) {
        if (!isInfoOutput) {
            return
        }
        TODO("not implemented")
    }

    open fun log(priority: Int, msg: String) {
        Log.i("MOCK_TAG", msg)
    }
}
