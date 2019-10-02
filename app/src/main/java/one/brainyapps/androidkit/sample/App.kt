/*
 * Developed by Serhii Pokrovskyi
 * e-mail: pokrovskyi.dev@gmail.com
 * Last modified: 9/30/19 3:09 PM
 * Copyright (c) 2019
 * All rights reserved
 */

package one.brainyapps.androidkit.sample

import android.app.Application
import one.brainyapps.androidkit.logger.DebugGalaxy
import one.brainyapps.androidkit.logger.Hubble

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        Hubble.add(DebugGalaxy())
    }
}