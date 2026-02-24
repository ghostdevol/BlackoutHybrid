package com.ghosttech.blackout.engine;

/**
 * MediationController routes all sensitive operations through a single,
 * controlled execution path. It validates state transitions, lifecycle
 * traversal, and boundary activation. Any illegal transition triggers
 * anomaly handling.
 *
 * Authoritative reference: Blackout Engine Logic Sheet (Daniel)
 */
public class MediationController {

    private EngineState currentState;
    private LifecyclePhase currentPhase;
    private final AnomalyHandler anomalyHandler;

    public MediationController(AnomalyHandler anomalyHandler) {
        this.currentState = EngineState.COLD;
        this.currentPhase = null;
        this.anomalyHandler = anomalyHandler;
    }

    /**
     * Attempts a state transition. Illegal transitions trigger anomaly handling.
     *
     * @param next The desired next state.
     */
    public void transitionState(EngineState next) {
        if (!currentState.isValidTransition(next)) {
            anomalyHandler.handle("Illegal state transition: " +
                    currentState + " → " + next);
            currentState = EngineState.LOCKED;
            return;
        }
        currentState = next;
    }

    /**
     * Attempts a lifecycle transition. Illegal transitions trigger anomaly handling.
     *
     * @param next The desired next lifecycle phase.
     */
    public void transitionPhase(LifecyclePhase next) {
        if (currentPhase == null) {
            // First phase must be UNLOCK
            if (next != LifecyclePhase.UNLOCK) {
                anomalyHandler.handle("Lifecycle must begin with UNLOCK");
                currentState = EngineState.LOCKED;
                return;
            }
            currentPhase = next;
            return;
        }

        if (!currentPhase.isValidTransition(next)) {
            anomalyHandler.handle("Illegal lifecycle transition: " +
                    currentPhase + " → " + next);
            currentState = EngineState.LOCKED;
            return;
        }

        currentPhase = next;
    }

    /**
     * Returns the current engine state.
     */
    public EngineState getCurrentState() {
        return currentState;
    }

    /**
     * Returns the current lifecycle phase.
     */
    public LifecyclePhase getCurrentPhase() {
        return currentPhase;
    }

    /**
     * Resets lifecycle phase after teardown.
     */
    public void resetLifecycle() {
        currentPhase = null;
    }
}
