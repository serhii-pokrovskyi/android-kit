/*
 * Developed by Serhii Pokrovskyi
 * e-mail: pokrovskyi.dev@gmail.com
 * Last modified: 4/26/20 11:13 AM
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

@Deprecated(
    message = "This BillingProcessor will not be developed anymore", ,
    level = DeprecationLevel.WARNING
)
internal class BillingProcessorImpl(context: Context) : BillingProcessor,
    SkuDetailsResponseListener, PurchasesUpdatedListener, BillingClientStateListener {

    private val billingClient: BillingClient = BillingClient.newBuilder(context)
        .setListener(this)
        .enablePendingPurchases()
        .build()

    private var setupListener: ((billingResult: BillingResult) -> Unit)? = null
    private var historyListener: ((result: PurchaseHistoryResult) -> Unit)? = null

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

        // setup listener
        setupListener?.invoke(billingResult)

        // query purchases history
        historyListener?.let {
            queryPurchaseHistory()
        }
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

    override val isReady: Boolean
        get() = billingClient.isReady

    override fun addSetupFinishedListener(listener: (billingResult: BillingResult) -> Unit): BillingProcessor {
        setupListener = listener
        return this
    }

    override fun addPurchaseHistoryListener(listener: (result: PurchaseHistoryResult) -> Unit): BillingProcessor {
        historyListener = listener
        return this
    }

    override fun queryPurchaseHistory() {
        checkBillingClientState()

        CoroutineScope(Dispatchers.Main).launch {
            val result: PurchaseHistoryResult =
                withContext(CoroutineScope(Dispatchers.IO).coroutineContext) {
                    billingClient.queryPurchaseHistory(BillingClient.SkuType.INAPP)
                }

            historyListener?.invoke(result)
        }
    }

    override fun release() {
        billingClient.endConnection()
    }

    private fun checkBillingClientState() {
        if (!isReady) {
            throw IllegalStateException("billingClient is not ready!")
        }
    }

    companion object {
        private const val TAG = "BillingProcessor"
    }
}
