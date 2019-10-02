/*
 * Developed by Serhii Pokrovskyi
 * e-mail: pokrovskyi.dev@gmail.com
 * Last modified: 9/30/19 11:26 PM
 * Copyright (c) 2019
 * All rights reserved
 */

package one.brainyapps.androidkit.logger

class DebugGalaxy : Galaxy() {

    //todo: think about some ignore list
    val ignore = listOf("q", "w")

    override val tag: String?
        get() = Throwable().stackTrace.first().className

}