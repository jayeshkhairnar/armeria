package com.ctrlshift.internal;

import java.util.Optional;

import javax.annotation.Nullable;

import com.ctrlshift.server.annotation.Default;

/**
 * Holds the default values used in annotation attributes.
 */
public final class DefaultValues {

    /**
     * A string constant defining unspecified values from users.
     *
     * @see Default#value()
     */
    public static final String UNSPECIFIED = "\n\t\t\n\t\t\n\000\001\002\n\t\t\t\t\n";

    /**
     * Returns whether the specified value is specified by a user.
     */
    public static boolean isSpecified(@Nullable String value) {
        return !UNSPECIFIED.equals(value);
    }

    /**
     * Returns whether the specified value is not specified by a user.
     */
    public static boolean isUnspecified(@Nullable String value) {
        return UNSPECIFIED.equals(value);
    }

    /**
     * Returns the specified value if it is specified by a user.
     */
    public static Optional<String> getSpecifiedValue(@Nullable String value) {
        return isSpecified(value) ? Optional.ofNullable(value) : Optional.empty();
    }

    private DefaultValues() {}
}
