/*
 * Developed by Serhii Pokrovskyi
 * e-mail: pokrovskyi.dev@gmail.com
 * Last modified: 10/2/19 10:12 PM
 * Copyright (c) 2019
 * All rights reserved
 */

package one.brainyapps.androidkit.sample.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import one.brainyapps.androidkit.logger.Hubble
import one.brainyapps.androidkit.sample.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Hubble.i("test log message")
    }
}
