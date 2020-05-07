/*
 * Developed by Serhii Pokrovskyi
 * e-mail: serg.pokrovskyi@gmail.com
 * Last modified: 5/7/20 7:11 PM
 * Copyright (c) 2020
 * All rights reserved
 */

package one.brainyapps.androidkit.billing

import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.Purchase

/**
 * Represents purchase result
 * @property[responseCode] - represents [BillingClient.BillingResponseCode]
 * @property[debugMessage] - debug message
 * @property[purchases] - list of purchases
 */
class PurchaseUpdateResult(
    val responseCode: Int,
    val debugMessage: String,
    val purchases: List<Purchase>?
)