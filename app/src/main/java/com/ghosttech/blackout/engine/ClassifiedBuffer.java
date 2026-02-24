package com.ghosttech.blackout.engine;

import java.util.Arrays;

/**
 * ClassifiedBuffer represents a zero-retention, explicitly managed memory region
 * used for sensitive operations inside the Blackout boundary.
 *
 * Rules enforced:
 *  - Explicit allocation
 *  - Explicit overwrite before release
 *  - No persistence
 *  - No external references
 *  - JVM GC cannot be trusted; manual wipe required
 *
 * Authoritative reference: Blackout Engine Logic Sheet (Daniel)
 */
public class ClassifiedBuffer {

    private byte[] data;
    private boolean valid;

    /**
     * Allocates a classified buffer of the given size.
     *
     * @param size Size in bytes.
     */
    public ClassifiedBuffer(int size) {
        this.data = new byte[size];
        this.valid = true;
    }

    /**
     * Writes data into the classified buffer.
     *
     * @param input The data to write.
     */
    public void write(byte[] input) {
        if (!valid) {
            throw new IllegalStateException("Attempt to write to invalidated buffer");
        }
        if (input.length > data.length) {
            throw new IllegalArgumentException("Input exceeds buffer size");
        }
        System.arraycopy(input, 0, data, 0, input.length);
    }

    /**
     * Reads the classified data.
     * Returns a copy to prevent external references.
     *
     * @return Copy of the buffer contents.
     */
    public byte[] read() {
        if (!valid) {
            throw new IllegalStateException("Attempt to read invalidated buffer");
        }
        return Arrays.copyOf(data, data.length);
    }

    /**
     * Wipes the buffer by overwriting all bytes with zero.
     */
    public void wipe() {
        if (data != null) {
            Arrays.fill(data, (byte) 0x00);
        }
        valid = false;
    }

    /**
     * Indicates whether the buffer is still valid.
     *
     * @return true if valid, false if wiped.
     */
    public boolean isValid() {
        return valid;
    }
}
