package com.ghosttech.blackout.engine;

/**
 * LifecyclePhase defines the required lifecycle traversal for all
 * sensitive Blackout operations.
 *
 * UNLOCK  - Load ephemeral key material and enter ACTIVE state.
 * EXECUTE - Perform the sensitive operation inside the boundary.
 * ERASE   - Deterministic teardown: wipe memory, destroy keys, return to COLD.
 *
 * Authoritative reference: Blackout Engine Logic Sheet (Daniel)
 */
public enum LifecyclePhase {

    UNLOCK,
    EXECUTE,
    ERASE;

    /**
     * Validates the correct lifecycle order.
     *
     * @param next The next lifecycle phase.
     * @return true if the transition is valid, false otherwise.
     */
    public boolean isValidTransition(LifecyclePhase next) {
        switch (this) {

            case UNLOCK:
                return next == EXECUTE;

            case EXECUTE:
                return next == ERASE;

            case ERASE:
                // ERASE is terminal; no valid transitions after teardown
                return false;

            default:
                return false;
        }
    }
}
