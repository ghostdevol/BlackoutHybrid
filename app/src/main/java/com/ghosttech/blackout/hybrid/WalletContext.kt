package com.ghosttech.blackout.hybrid

import org.web3j.crypto.Credentials
import org.web3j.crypto.WalletUtils
import org.web3j.crypto.MnemonicUtils
import org.web3j.crypto.ECKeyPair
import org.web3j.crypto.Keys

class WalletContext {

    var mnemonic: String = ""
    var privateKey: String = ""
    var address: String = ""

    fun createWallet() {
        mnemonic = MnemonicUtils.generateMnemonic()
        val seed = MnemonicUtils.generateSeed(mnemonic, "")
        val keyPair = ECKeyPair.create(seed.copyOfRange(0, 32))
        privateKey = keyPair.privateKey.toString(16)
        address = "0x" + Keys.getAddress(keyPair)
    }

    fun importWallet(m: String) {
        mnemonic = m
        val seed = MnemonicUtils.generateSeed(mnemonic, "")
        val keyPair = ECKeyPair.create(seed.copyOfRange(0, 32))
        privateKey = keyPair.privateKey.toString(16)
        address = "0x" + Keys.getAddress(keyPair)
    }

    fun getAddress(): String {
        return address
    }
}