package com.ctrlshift.server.logging;

import static com.google.common.base.Preconditions.checkArgument;
import static com.ctrlshift.server.logging.AccessLogFormats.parseCustom;
import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.function.Consumer;

import com.ctrlshift.commons.logging.RequestLog;

/**
 * Access log writers.
 */
public final class AccessLogWriters {

    /**
     * Returns an access log writer with a common format.
     */
    public static Consumer<RequestLog> common() {
        return requestLog -> AccessLogger.write(AccessLogFormats.COMMON, requestLog);
    }

    /**
     * Returns an access log writer with a combined format.
     */
    public static Consumer<RequestLog> combined() {
        return requestLog -> AccessLogger.write(AccessLogFormats.COMBINED, requestLog);
    }

    /**
     * Returns disabled access log writer.
     */
    public static Consumer<RequestLog> disabled() {
        return requestLog -> { /* No operation. */ };
    }

    /**
     * Returns an access log writer with the specified {@code formatStr}.
     */
    public static Consumer<RequestLog> custom(String formatStr) {
        final List<AccessLogComponent> accessLogFormat = parseCustom(requireNonNull(formatStr, "formatStr"));
        checkArgument(!accessLogFormat.isEmpty(), "Invalid access log format string: " + formatStr);
        return requestLog -> AccessLogger.write(accessLogFormat, requestLog);
    }

    private AccessLogWriters() {}
}
