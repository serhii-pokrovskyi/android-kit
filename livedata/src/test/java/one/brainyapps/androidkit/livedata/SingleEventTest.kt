/*
 * Developed by Serhii Pokrovskyi
 * e-mail: pokrovskyi.dev@gmail.com
 * Last modified: 9/28/19 11:49 AM
 * Copyright (c) 2019
 * All rights reserved
 */

package one.brainyapps.androidkit.livedata

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import one.brainyapps.androidkit.livedata.util.TestLifecycleOwner
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class SingleEventTest {
    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    private lateinit var obs: String

    private lateinit var owner: TestLifecycleOwner
    private lateinit var singleEvent: SingleEvent<String>
    private lateinit var observerOne: Observer<String>

    @Before
    fun initial() {
        obs = "test"
        owner = TestLifecycleOwner()
        singleEvent = SingleEvent()
        observerOne = mock()
    }

    @Test
    fun simpleObserve() {
        // initial
        owner.onStart()
        singleEvent.observe(owner, observerOne)

        singleEvent.value = obs

        verify(observerOne, times(1)).onChanged(obs)
    }

    @Test
    fun valueBeforeObserve() {
        // initial
        owner.onStart()
        singleEvent.value = obs

        singleEvent.observe(owner, observerOne)

        verify(observerOne, times(1)).onChanged(obs)
    }

    @Test
    fun valueBeforeOwnerCreated() {
        // initial
        owner.onDestroy()

        singleEvent.value = obs
        owner.onCreate()

        singleEvent.observe(owner, observerOne)

        verify(observerOne, never()).onChanged(obs)

        // checking start
        owner.onStart()
        verify(observerOne, times(1)).onChanged(obs)
    }

    @Test
    fun valueAfterStop() {
        // initial
        owner.onStart()
        singleEvent.observe(owner, observerOne)

        owner.onStop()

        singleEvent.value = obs

        verify(observerOne, never()).onChanged(obs)
    }

    @Test
    fun complexTest() {
        // initial
        owner.onDestroy()
        singleEvent.value = obs

        // creating
        owner.onCreate()
        singleEvent.observe(owner, observerOne)
        verify(observerOne, never()).onChanged(obs)

        // starting
        owner.onStart()
        verify(observerOne, times(1)).onChanged(obs)

        // emulate owner recreation
        singleEvent.removeObservers(owner)
        owner.onDestroy()

        // creating
        owner.onCreate()
        singleEvent.observe(owner, observerOne)
        verify(observerOne, times(1)).onChanged(obs)

        // starting
        owner.onStart()
        verify(observerOne, times(1)).onChanged(obs)

        // value
        singleEvent.value = obs
        verify(observerOne, times(2)).onChanged(obs)

        // emulate owner recreation
        singleEvent.removeObservers(owner)
        owner.onDestroy()

        // set value when view destroyed
        singleEvent.value = obs

        // creating
        owner.onCreate()
        verify(observerOne, times(2)).onChanged(obs)
        // observing
        singleEvent.observe(owner, observerOne)
        verify(observerOne, times(2)).onChanged(obs)

        // starting
        owner.onStart()
        verify(observerOne, times(3)).onChanged(obs)
    }
}