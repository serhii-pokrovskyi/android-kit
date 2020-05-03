/*
 * Developed by Serhii Pokrovskyi
 * e-mail: serg.pokrovskyi@gmail.com
 * Last modified: 5/3/20 9:25 PM
 * Copyright (c) 2020
 * All rights reserved
 */

package one.brainyapps.androidkit.livedata

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import one.brainyapps.androidkit.livedata.util.TestLifecycleOwner
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class SingleCallTest {

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    private var testValue: Int = 0

    private lateinit var owner: TestLifecycleOwner
    private lateinit var singleCall: SingleCall
    private lateinit var observer: Observer<Unit>

    @Before
    fun initial() {
        testValue = 0
        owner = TestLifecycleOwner()
        singleCall = SingleCall()

        observer = Observer {
            testValue++
        }
    }

    @Test
    fun simpleObserve() {
        // initial
        owner.onStart()
        singleCall.observe(owner, observer)

        singleCall.call()

        assertEquals(testValue, 1)
    }

    @Test
    fun callBeforeObserve() {
        // initial
        owner.onStart()

        singleCall.call()

        singleCall.observe(owner, observer)

        assertEquals(testValue, 1)
    }

    @Test
    fun callBeforeOwnerCreated() {
        // initial
        owner.onDestroy()

        // calling
        singleCall.call()

        owner.onCreate()
        singleCall.observe(owner, observer)

        assertEquals(testValue, 0)

        owner.onStart()

        assertEquals(testValue, 1)
    }

    @Test
    fun callAfterStop() {
        // initial
        owner.onStart()
        singleCall.observe(owner, observer)

        owner.onStop()

        singleCall.call()

        assertEquals(testValue, 0)
    }

    @Test
    fun complexTest() {
        // initial
        owner.onDestroy()
        singleCall.call()

        // creating
        owner.onCreate()
        singleCall.observe(owner, observer)
        assertEquals(testValue, 0)

        // starting
        owner.onStart()
        assertEquals(testValue, 1)

        // emulate owner recreation
        singleCall.removeObservers(owner)
        owner.onDestroy()

        // creating
        owner.onCreate()
        singleCall.observe(owner, observer)
        assertEquals(testValue, 1)

        // starting
        owner.onStart()
        assertEquals(testValue, 1)

        // call
        singleCall.call()
        assertEquals(testValue, 2)

        // emulate owner recreation
        singleCall.removeObservers(owner)
        owner.onDestroy()

        // call when owner destroyed
        singleCall.call()

        // creating
        owner.onCreate()
        assertEquals(testValue, 2)
        // observing
        singleCall.observe(owner, observer)
        assertEquals(testValue, 2)

        // starting
        owner.onStart()
        assertEquals(testValue, 3)
    }
}