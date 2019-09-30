/*
 * Developed by Serhii Pokrovskyi
 * e-mail: pokrovskyi.dev@gmail.com
 * Last modified: 9/30/19 3:41 PM
 * Copyright (c) 2019
 * All rights reserved
 */

package one.brainyapps.androidkit.logger

object Hubble {

    @Volatile
    private var galaxies = arrayListOf<Galaxy>()

    fun add(galaxy: Galaxy) {
        synchronized(this) {
            galaxies.add(galaxy)
        }
    }

    fun i(msg: String) {
        galaxies.forEach {
            it.i(msg)
        }
    }

    fun i(msg: String, throwable: Throwable) {
        galaxies.forEach {
            it.i(msg, throwable)
        }
    }
}
