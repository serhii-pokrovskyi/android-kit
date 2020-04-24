/*
 * Developed by Serhii Pokrovskyi
 * e-mail: pokrovskyi.dev@gmail.com
 * Last modified: 4/24/20 12:54 PM
 * Copyright (c) 2020
 * All rights reserved
 */

package one.brainyapps.androidkit.billing

import android.content.Context
import android.util.Log
import com.android.billingclient.api.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class BillingProcessorImpl(context: Context) : BillingProcessor,
    SkuDetailsResponseListener, PurchasesUpdatedListener, BillingClientStateListener {

    private val billingClient: BillingClient = BillingClient.newBuilder(context)
        .setListener(this)
        .enablePendingPurchases()
        .build()

    init {
        billingClient.startConnection(this)
    }

    override fun onBillingServiceDisconnected() {
        Log.d(TAG, "onBillingServiceDisconnected")
    }

    override fun onBillingSetupFinished(billingResult: BillingResult) {
        val responseCode = billingResult.responseCode
        val debugMessage = billingResult.debugMessage
        Log.d(TAG, "onBillingSetupFinished: $responseCode $debugMessage")

        //TODO: query purchases
    }

    override fun onSkuDetailsResponse(
        billingResult: BillingResult?,
        skuDetailsList: MutableList<SkuDetails>?
    ) {
        if (billingResult == null) {
            Log.wtf(TAG, "onSkuDetailsResponse: null BillingResult")
            return
        }
        val responseCode = billingResult.responseCode
        val debugMessage = billingResult.debugMessage

        when (responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                Log.i(TAG, "onSkuDetailsResponse: $responseCode $debugMessage")
                //TODO("Not yet implemented")
            }
            BillingClient.BillingResponseCode.SERVICE_DISCONNECTED,
            BillingClient.BillingResponseCode.SERVICE_UNAVAILABLE,
            BillingClient.BillingResponseCode.BILLING_UNAVAILABLE,
            BillingClient.BillingResponseCode.ITEM_UNAVAILABLE,
            BillingClient.BillingResponseCode.DEVELOPER_ERROR,
            BillingClient.BillingResponseCode.ERROR -> {
                Log.e(TAG, "onSkuDetailsResponse: $responseCode $debugMessage")
            }
            BillingClient.BillingResponseCode.USER_CANCELED,
            BillingClient.BillingResponseCode.FEATURE_NOT_SUPPORTED,
            BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED,
            BillingClient.BillingResponseCode.ITEM_NOT_OWNED -> {
                // These response codes are not expected.
                Log.wtf(TAG, "onSkuDetailsResponse: $responseCode $debugMessage")
            }
        }
    }

    override fun onPurchasesUpdated(p0: BillingResult?, p1: MutableList<Purchase>?) {
        TODO("Not yet implemented")
    }

    override fun queryPurchaseHistory(listener: (result: PurchaseHistoryResult) -> Unit) {
        CoroutineScope(Dispatchers.Main).launch {
            val result: PurchaseHistoryResult =
                withContext(CoroutineScope(Dispatchers.IO).coroutineContext) {
                    billingClient.queryPurchaseHistory(BillingClient.SkuType.INAPP)
                }

            listener.invoke(result)
        }
    }

    override fun release() {
        billingClient.endConnection()
    }

    companion object {
        private const val TAG = "BillingProcessor"
    }
}
