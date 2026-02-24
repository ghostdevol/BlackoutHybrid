package com.ghosttech.blackout.engine;

/**
 * EngineState defines the valid operational states of the Blackout engine.
 * 
 * COLD      - Idle, no sensitive material in memory.
 * ACTIVE    - Engine executing lifecycle operations.
 * TEARDOWN  - Deterministic destruction of classified material.
 * LOCKED    - Security halt state; requires manual reset.
 *
 * Authoritative reference: Blackout Engine Logic Sheet (Daniel)
 */
public enum EngineState {

    COLD,
    ACTIVE,
    TEARDOWN,
    LOCKED;

    /**
     * Validates whether a transition between states is allowed.
     *
     * @param next The desired next state.
     * @return true if the transition is valid, false otherwise.
     */
    public boolean isValidTransition(EngineState next) {
        switch (this) {

            case COLD:
                return next == ACTIVE;

            case ACTIVE:
                return next == COLD || next == LOCKED;

            case TEARDOWN:
                // TEARDOWN always ends in COLD
                return next == COLD;

            case LOCKED:
                // LOCKED only returns to COLD via manual reset
                return next == COLD;

            default:
                return false;
        }
    }
}
