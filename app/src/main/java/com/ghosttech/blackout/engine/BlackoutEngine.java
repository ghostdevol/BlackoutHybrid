package com.ghosttech.blackout.engine;

import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * BlackoutEngine orchestrates all components of the classified-tier
 * Blackout execution model. It enforces:
 *
 *  - State transitions
 *  - Lifecycle traversal
 *  - Boundary activation
 *  - Ephemeral key loading
 *  - Classified buffer allocation
 *  - Controlled execution
 *  - Deterministic teardown
 *
 * Authoritative reference: Blackout Engine Logic Sheet (Daniel)
 */
public class BlackoutEngine {

    private final Boundary boundary;
    private final SecurityController securityController;
    private final MediationController mediationController;
    private final TeardownManager teardownManager;

    public BlackoutEngine(PublicKey publicKey) {
        AnomalyHandler anomalyHandler = new AnomalyHandler();
        this.mediationController = new MediationController(anomalyHandler);
        this.boundary = new Boundary();
        this.securityController = new SecurityController(publicKey);
        this.teardownManager = new TeardownManager(boundary, securityController, mediationController);
    }

    /**
     * Begins the UNLOCK phase:
     *  - Transition state to ACTIVE
     *  - Activate boundary
     *  - Load ephemeral key
     */
    public void unlock(PrivateKey ephemeralKey) {
        mediationController.transitionState(EngineState.ACTIVE);
        mediationController.transitionPhase(LifecyclePhase.UNLOCK);

        boundary.activate();
        securityController.loadEphemeralKey(ephemeralKey);
    }

    /**
     * Executes a sensitive operation inside the boundary.
     *
     * @param input Data to process.
     * @return Processed output.
     */
    public byte[] execute(byte[] input) {
        mediationController.transitionPhase(LifecyclePhase.EXECUTE);

        ClassifiedBuffer buffer = boundary.allocateBuffer(input.length);
        buffer.write(input);

        // Example operation: sign the data
        try {
            return securityController.sign(buffer.read());
        } catch (Exception e) {
            throw new RuntimeException("Execution failed", e);
        }
    }

    /**
     * Performs deterministic teardown:
     *  - Wipe buffers
     *  - Destroy keys
     *  - Reset lifecycle
     *  - Return to COLD
     */
    public void erase() {
        mediationController.transitionPhase(LifecyclePhase.ERASE);
        teardownManager.teardown();
    }

    /**
     * Returns the current engine state.
     */
    public EngineState getState() {
        return mediationController.getCurrentState();
    }
}
