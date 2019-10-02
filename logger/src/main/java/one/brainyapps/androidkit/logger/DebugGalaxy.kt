/*
 * Developed by Serhii Pokrovskyi
 * e-mail: pokrovskyi.dev@gmail.com
 * Last modified: 10/2/19 10:34 PM
 * Copyright (c) 2019
 * All rights reserved
 */

package one.brainyapps.androidkit.logger

import android.os.Build

class DebugGalaxy : Galaxy() {

    //todo: think about some ignore list
    val ignore = arrayListOf(
        Hubble::class.java.name,
        Galaxy::class.java.name,
        DebugGalaxy::class.java.name
    )

    override val tag: String?
        get() = Throwable().stackTrace.first {
            it.className !in ignore
        }.let {
            var tag = it.className.substringAfterLast('.')

            // length check
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                if (tag.length > 23) {
                    // making it shorter
                    tag.substring(0, 23)
                }
            }

            return@let tag
        }

}