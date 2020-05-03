/*
 * Developed by Serhii Pokrovskyi
 * e-mail: serg.pokrovskyi@gmail.com
 * Last modified: 5/3/20 9:25 PM
 * Copyright (c) 2020
 * All rights reserved
 */

package one.brainyapps.androidkit.sample.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import one.brainyapps.androidkit.billing.BillingProcessor
import one.brainyapps.androidkit.billing.BillingProcessorLifecycle
import one.brainyapps.androidkit.sample.R

class MainActivity : AppCompatActivity() {

    private lateinit var billingProcessorLifecycle: BillingProcessorLifecycle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        billingProcessorLifecycle = BillingProcessorLifecycle.getInstance(application)
        lifecycle.addObserver(billingProcessorLifecycle)

        billingProcessorLifecycle.purchasesHistory.observe(this, Observer {
            //
        })

        val billingProcessor = BillingProcessor.new(this)
            .addPurchaseHistoryListener {
                //todo: test
            }
    }
}
