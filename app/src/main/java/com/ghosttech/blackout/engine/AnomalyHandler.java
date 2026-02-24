package com.ghosttech.blackout.engine;

import android.util.Log;

/**
 * AnomalyHandler defines how the Blackout engine reacts to violations of
 * state rules, lifecycle rules, boundary rules, or security constraints.
 *
 * Rules enforced:
 *  - No silent failures.
 *  - All violations must be logged.
 *  - Engine must enter LOCKED state after anomaly.
 *  - No recovery without full teardown.
 *
 * Authoritative reference: Blackout Engine Logic Sheet (Daniel)
 */
public class AnomalyHandler {

    private static final String TAG = "Blackout-Anomaly";

    /**
     * Handles an anomaly by logging it and triggering LOCKED state behavior.
     *
     * @param message Description of the anomaly.
     */
    public void handle(String message) {
        Log.e(TAG, "ANOMALY DETECTED: " + message);

        // In a classified-tier system, this would trigger:
        // - immediate teardown
        // - key destruction
        // - boundary wipe
        // - state lock
        //
        // For now, we log the anomaly. The Engine class will enforce LOCKED.
    }
}
