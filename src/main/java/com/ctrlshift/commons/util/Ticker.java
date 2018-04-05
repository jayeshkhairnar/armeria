package com.ctrlshift.commons.util;

/**
 * A time source; returns a time value representing the number of nanoseconds elapsed since some
 * fixed but arbitrary point in time.
 *
 * <p><b>Warning:</b> this interface can only be used to measure elapsed time, not wall time.
 *
 * @author Kevin Bourrillion
 */
@FunctionalInterface
public interface Ticker {
    /**
     * Returns the number of nanoseconds elapsed since this ticker's fixed point of reference.
     */
    long read();

    /**
     * A ticker that reads the current time using {@link System#nanoTime}.
     */
    static Ticker systemTicker() {
        return System::nanoTime;
    }
}