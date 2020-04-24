/*
 * Developed by Serhii Pokrovskyi
 * e-mail: pokrovskyi.dev@gmail.com
 * Last modified: 4/24/20 6:10 PM
 * Copyright (c) 2020
 * All rights reserved
 */

package one.brainyapps.androidkit.sample.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import one.brainyapps.androidkit.billing.BillingProcessor
import one.brainyapps.androidkit.sample.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val billingProcessor = BillingProcessor.new(this)
            .addPurchaseHistoryListener {
                //todo: test
            }
    }
}
