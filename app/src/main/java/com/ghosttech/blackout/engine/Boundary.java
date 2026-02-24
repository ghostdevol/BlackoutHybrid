package com.ghosttech.blackout.engine;

/**
 * Boundary represents the classified execution boundary for the Blackout engine.
 * All sensitive operations, buffers, and ephemeral key material must remain
 * inside this boundary. No persistence, no leakage, no external references.
 *
 * Authoritative reference: Blackout Engine Logic Sheet (Daniel)
 */
public class Boundary {

    private boolean active;
    private ClassifiedBuffer buffer;

    public Boundary() {
        this.active = false;
        this.buffer = null;
    }

    /**
     * Activates the boundary for sensitive operations.
     * Must be called during UNLOCK → ACTIVE transition.
     */
    public void activate() {
        this.active = true;
    }

    /**
     * Allocates a classified buffer inside the boundary.
     * @param size Size of the buffer in bytes.
     * @return The allocated ClassifiedBuffer.
     */
    public ClassifiedBuffer allocateBuffer(int size) {
        if (!active) {
            throw new IllegalStateException("Boundary not active");
        }
        this.buffer = new ClassifiedBuffer(size);
        return this.buffer;
    }

    /**
     * Returns the current classified buffer.
     * @return ClassifiedBuffer or null if none allocated.
     */
    public ClassifiedBuffer getBuffer() {
        return this.buffer;
    }

    /**
     * Destroys all classified material inside the boundary.
     * Called during ERASE → COLD transition.
     */
    public void destroy() {
        if (buffer != null) {
            buffer.wipe();
            buffer = null;
        }
        active = false;
    }

    /**
     * Indicates whether the boundary is active.
     * @return true if active, false otherwise.
     */
    public boolean isActive() {
        return active;
    }
}
