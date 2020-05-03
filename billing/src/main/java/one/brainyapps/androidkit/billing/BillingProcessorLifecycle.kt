/*
 * Developed by Serhii Pokrovskyi
 * e-mail: serg.pokrovskyi@gmail.com
 * Last modified: 5/3/20 11:40 PM
 * Copyright (c) 2020
 * All rights reserved
 */

package one.brainyapps.androidkit.billing

import android.app.Activity
import android.app.Application
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.OnLifecycleEvent
import com.android.billingclient.api.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import one.brainyapps.androidkit.livedata.SingleEvent

class BillingProcessorLifecycle private constructor(
    private val app: Application
) : LifecycleObserver, PurchasesUpdatedListener, BillingClientStateListener,
    SkuDetailsResponseListener {

    /**
     * The purchase event is observable. Only one observer will be notified.
     */
    val purchaseUpdateEvent = SingleEvent<List<Purchase>>()

    /**
     * The purchase history event is observable.
     */
    val purchasesHistory = MutableLiveData<List<PurchaseHistoryRecord>>()

    /**
     * SkuDetails for all known SKUs.
     */
    val skusWithSkuDetails = MutableLiveData<Map<String, SkuDetails>>()

    /**
     * Instantiate a new BillingClient instance.
     */
    private lateinit var billingClient: BillingClient

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun create() {
        Log.d(TAG, "ON_CREATE")
        // Create a new BillingClient in onCreate().
        // Since the BillingClient can only be used once, we need to create a new instance
        // after ending the previous connection to the Google Play Store in onDestroy().
        billingClient = BillingClient.newBuilder(app.applicationContext)
            .setListener(this)
            .enablePendingPurchases() // Not used for subscriptions.
            .build()
        if (!billingClient.isReady) {
            Log.d(TAG, "BillingClient: Start connection...")
            billingClient.startConnection(this)
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun destroy() {
        Log.d(TAG, "ON_DESTROY")
        if (billingClient.isReady) {
            Log.d(TAG, "BillingClient can only be used once -- closing connection")
            // BillingClient can only be used once.
            // After calling endConnection(), we must create a new BillingClient.
            billingClient.endConnection()
        }
    }

    override fun onBillingSetupFinished(billingResult: BillingResult) {
        val responseCode = billingResult.responseCode
        val debugMessage = billingResult.debugMessage
        Log.d(TAG, "onBillingSetupFinished: $responseCode $debugMessage")
        if (responseCode == BillingClient.BillingResponseCode.OK) {
            // The billing client is ready. You can query purchases here.
            //TODO: querySkuDetails()

            // purchases history
            CoroutineScope(Dispatchers.IO).launch {
                queryPurchaseHistory()
            }
        }
    }

    override fun onBillingServiceDisconnected() {
        Log.d(TAG, "onBillingServiceDisconnected")
        // TODO: Try connecting again with exponential backoff.
        // billingClient.startConnection(this)
    }

    /**
     * Receives the result from [querySkuDetails].
     *
     * Store the SkuDetails and post them in the [skusWithSkuDetails]. This allows other parts
     * of the app to use the [SkuDetails] to show SKU information and make purchases.
     */
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
                if (skuDetailsList == null) {
                    Log.w(TAG, "onSkuDetailsResponse: null SkuDetails list")
                    skusWithSkuDetails.postValue(emptyMap())
                } else
                    skusWithSkuDetails.postValue(HashMap<String, SkuDetails>().apply {
                        for (details in skuDetailsList) {
                            put(details.sku, details)
                        }
                    }.also { postedValue ->
                        Log.i(TAG, "onSkuDetailsResponse: count ${postedValue.size}")
                    })
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

    /**
     * Called by the Billing Library when new purchases are detected.
     */
    override fun onPurchasesUpdated(
        billingResult: BillingResult?,
        purchases: MutableList<Purchase>?
    ) {
        if (billingResult == null) {
            Log.wtf(TAG, "onPurchasesUpdated: null BillingResult")
            return
        }
        val responseCode = billingResult.responseCode
        val debugMessage = billingResult.debugMessage
        Log.d(TAG, "onPurchasesUpdated: $responseCode $debugMessage")
        when (responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                if (purchases == null) {
                    Log.d(TAG, "onPurchasesUpdated: null purchase list")
                    processPurchases(null)
                } else {
                    processPurchases(purchases)
                }
            }
            BillingClient.BillingResponseCode.USER_CANCELED -> {
                Log.i(TAG, "onPurchasesUpdated: User canceled the purchase")
            }
            BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> {
                Log.i(TAG, "onPurchasesUpdated: The user already owns this item")
            }
            BillingClient.BillingResponseCode.DEVELOPER_ERROR -> {
                Log.e(
                    TAG, "onPurchasesUpdated: Developer error means that Google Play " +
                            "does not recognize the configuration. If you are just getting started, " +
                            "make sure you have configured the application correctly in the " +
                            "Google Play Console. The SKU product ID must match and the APK you " +
                            "are using must be signed with release keys."
                )
            }
        }
    }

    suspend fun queryPurchaseHistory() {
        if (!billingClient.isReady) {
            Log.e(TAG, "queryPurchaseHistory: BillingClient is not ready")
        }

        val result = billingClient.queryPurchaseHistory(BillingClient.SkuType.INAPP)

        if (result == null) {
            Log.i(TAG, "queryPurchaseHistory: null purchase result")
            processPurchaseHistory(emptyList())
        } else {
            if (result.purchaseHistoryRecordList == null) {
                Log.i(TAG, "queryPurchaseHistory: null purchase list")
                processPurchaseHistory(emptyList())
            } else {
                processPurchaseHistory(result.purchaseHistoryRecordList)
            }
        }
    }

    suspend fun launchInAppBillingBySku(activity: Activity, sku: String) {
        val skuParams = SkuDetailsParams.newBuilder()
            .setType(BillingClient.SkuType.INAPP)
            .setSkusList(arrayListOf(sku))
            .build()

        val skuResult = billingClient.querySkuDetails(skuParams)

        if (skuResult.skuDetailsList.isNullOrEmpty()) {
            // sku details list null or empty
            Log.e(
                TAG, "querySkuDetails: " +
                        "${skuResult.billingResult.responseCode} ${skuResult.billingResult.debugMessage}"
            )
            return
        }

        val skuDetails = skuResult.skuDetailsList!!.first()

        val billingFlowParams = BillingFlowParams.newBuilder()
            .setSkuDetails(skuDetails)
            .build()

        billingClient.launchBillingFlow(activity, billingFlowParams)
    }

    /**
     * Send purchase SingleLiveEvent and update purchases LiveData.
     *
     * The SingleLiveEvent will trigger network call to verify the subscriptions on the sever.
     * The LiveData will allow Google Play settings UI to update based on the latest purchase data.
     */
    private fun processPurchases(purchasesList: List<Purchase>?) {
        Log.d(TAG, "processPurchases: ${purchasesList?.size} purchase(s)")
        purchaseUpdateEvent.postValue(purchasesList)
    }

    private fun processPurchaseHistory(historyList: List<PurchaseHistoryRecord>?) {
        CoroutineScope(Dispatchers.Main).launch {
            purchasesHistory.value = historyList
        }
    }

    companion object {
        private const val TAG = "BillingProcessor"

        @Volatile
        private var INSTANCE: BillingProcessorLifecycle? = null

        fun getInstance(app: Application): BillingProcessorLifecycle =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: BillingProcessorLifecycle(app).also { INSTANCE = it }
            }
    }
}