/*
 * Developed by Serhii Pokrovskyi
 * e-mail: serg.pokrovskyi@gmail.com
 * Last modified: 5/26/20 3:05 PM
 * Copyright (c) 2020
 * All rights reserved
 */

package one.brainyapps.androidkit.extensionsktx

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction

/**
 * Simplifies adding fragment to container
 */
fun Fragment.addToContainer(
    manager: FragmentManager,
    containerId: Int,
    allowStateLoss: Boolean = false
) {
    val transaction: FragmentTransaction = manager.beginTransaction()
    transaction.add(containerId, this)
    if (allowStateLoss) {
        transaction.commitAllowingStateLoss()
    } else {
        transaction.commitNow()
    }
}