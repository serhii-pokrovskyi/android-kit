/*
 * Developed by Serhii Pokrovskyi
 * e-mail: pokrovskyi.dev@gmail.com
 * Last modified: 9/27/19 11:45 PM
 * Copyright (c) 2019
 * All rights reserved
 */

package one.brainyapps.androidkit.coroutines

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * //todo: add javadoc
 */
val scopeUi
    get() = CoroutineScope(Dispatchers.Main)

/**
 * //todo: add javadoc
 */
val scopeIo
    get() = CoroutineScope(Dispatchers.IO)

/**
 * //todo: add javadoc
 */
val scopeDefault
    get() = CoroutineScope(Dispatchers.Default)

/**
 * //todo: add javadoc
 */
fun launchUi(f: suspend (() -> Unit)) {
    CoroutineScope(Dispatchers.Main).launch { f() }
}

/**
 * //todo: add javadoc
 */
fun launchIo(f: suspend (() -> Unit)) {
    CoroutineScope(Dispatchers.IO).launch { f() }
}

/**
 * //todo: add javadoc
 */
suspend fun awaitIo(f: suspend (() -> Unit)) {
    withContext(CoroutineScope(Dispatchers.IO).coroutineContext) { f() }
}

/**
 * //todo: add javadoc
 */
fun launchDefault(f: suspend (() -> Unit)) {
    CoroutineScope(Dispatchers.Default).launch { f() }
}

/**
 * //todo: add javadoc
 */
suspend fun awaitDefault(f: suspend (() -> Unit)) {
    withContext(CoroutineScope(Dispatchers.Default).coroutineContext) { f() }
}