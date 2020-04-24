/*
 * Developed by Serhii Pokrovskyi
 * e-mail: pokrovskyi.dev@gmail.com
 * Last modified: 4/24/20 12:37 PM
 * Copyright (c) 2020
 * All rights reserved
 */

package one.brainyapps.androidkit.billing

import android.content.Context
import com.android.billingclient.api.PurchaseHistoryResult

interface BillingProcessor {

    fun queryPurchaseHistory(listener: (result: PurchaseHistoryResult) -> Unit)

    /**
     * Release billing client if you don't need it anymore
     */
    fun release()

    companion object {
        fun new(context: Context): BillingProcessor {
            return BillingProcessorImpl(context)
        }
    }
}