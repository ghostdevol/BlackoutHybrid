package com.ghosttech.blackout.engine;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;

/**
 * SecurityController manages ephemeral key material for the Blackout engine.
 *
 * Rules enforced:
 *  - Private keys exist ONLY during ACTIVE state.
 *  - Keys must be destroyed during ERASE.
 *  - Verification uses public key only.
 *  - No persistence, no caching, no external references.
 *
 * Authoritative reference: Blackout Engine Logic Sheet (Daniel)
 */
public class SecurityController {

    private PrivateKey ephemeralPrivateKey;
    private PublicKey publicKey;

    public SecurityController(PublicKey publicKey) {
        this.publicKey = publicKey;
    }

    /**
     * Loads ephemeral private key material.
     * Called during UNLOCK → ACTIVE transition.
     */
    public void loadEphemeralKey(PrivateKey privateKey) {
        this.ephemeralPrivateKey = privateKey;
    }

    /**
     * Signs data using the ephemeral private key.
     *
     * @param data The data to sign.
     * @return Signature bytes.
     */
    public byte[] sign(byte[] data) throws Exception {
        if (ephemeralPrivateKey == null) {
            throw new IllegalStateException("Ephemeral private key not loaded");
        }

        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(ephemeralPrivateKey);
        signature.update(data);
        return signature.sign();
    }

    /**
     * Verifies a signature using the public key.
     *
     * @param data The original data.
     * @param sig  The signature to verify.
     * @return true if valid, false otherwise.
     */
    public boolean verify(byte[] data, byte[] sig) throws Exception {
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initVerify(publicKey);
        signature.update(data);
        return signature.verify(sig);
    }

    /**
     * Destroys ephemeral private key material.
     * Called during ERASE → COLD transition.
     */
    public void destroyEphemeralKey() {
        if (ephemeralPrivateKey != null) {
            // JVM cannot wipe key material, but we can drop references.
            ephemeralPrivateKey = null;
        }
    }
}
