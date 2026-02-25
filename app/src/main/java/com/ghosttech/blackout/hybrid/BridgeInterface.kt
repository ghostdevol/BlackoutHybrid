package com.blackout

import android.webkit.JavascriptInterface

class BridgeInterface(private val wallet: WalletContext) {

    @JavascriptInterface
    fun createWallet(): String {
        wallet.createWallet()
        return wallet.address
    }

    @JavascriptInterface
    fun importWallet(mnemonic: String): String {
        wallet.importWallet(mnemonic)
        return wallet.address
    }

    @JavascriptInterface
    fun getAddress(): String {
        return wallet.getAddress()
    }
}
