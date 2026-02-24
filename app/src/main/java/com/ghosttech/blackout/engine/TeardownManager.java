package com.ghosttech.blackout.engine;

/**
 * TeardownManager performs deterministic teardown of all classified
 * materials and engine components. This is the only legal recovery
 * path from ACTIVE, EXECUTE, ERASE, or LOCKED states.
 *
 * Rules enforced:
 *  - All classified buffers must be wiped.
 *  - All ephemeral keys must be destroyed.
 *  - Boundary must be deactivated.
 *  - Lifecycle must reset.
 *  - Engine returns to COLD state.
 *
 * Authoritative reference: Blackout Engine Logic Sheet (Daniel)
 */
public class TeardownManager {

    private final Boundary boundary;
    private final SecurityController securityController;
    private final MediationController mediationController;

    public TeardownManager(
            Boundary boundary,
            SecurityController securityController,
            MediationController mediationController
    ) {
        this.boundary = boundary;
        this.securityController = securityController;
        this.mediationController = mediationController;
    }

    /**
     * Performs full teardown:
     *  - Wipe classified buffers
     *  - Destroy ephemeral keys
     *  - Reset lifecycle
     *  - Deactivate boundary
     *  - Return engine to COLD
     */
    public void teardown() {
        // 1. Destroy ephemeral key material
        securityController.destroyEphemeralKey();

        // 2. Wipe classified buffers and deactivate boundary
        boundary.destroy();

        // 3. Reset lifecycle
        mediationController.resetLifecycle();

        // 4. Return engine to COLD
        mediationController.transitionState(EngineState.COLD);
    }
}
