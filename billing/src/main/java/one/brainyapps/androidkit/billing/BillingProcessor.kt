/*
 * Developed by Serhii Pokrovskyi
 * e-mail: pokrovskyi.dev@gmail.com
 * Last modified: 4/26/20 11:13 AM
 * Copyright (c) 2020
 * All rights reserved
 */

package one.brainyapps.androidkit.billing

import android.content.Context
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PurchaseHistoryResult

@Deprecated(
    message = "This billing wrapper will not be developed anymore",
    level = DeprecationLevel.WARNING
)
interface BillingProcessor {

    val isReady: Boolean

    fun addSetupFinishedListener(listener: (billingResult: BillingResult) -> Unit): BillingProcessor

    fun addPurchaseHistoryListener(listener: (result: PurchaseHistoryResult) -> Unit): BillingProcessor

    fun queryPurchaseHistory()

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