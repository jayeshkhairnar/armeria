package com.ctrlshift.commons.util;

import java.util.concurrent.CompletionStage;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides the common actions that are useful when handling a {@link CompletionStage}.
 */
public final class CompletionActions {

    private static final Logger logger = LoggerFactory.getLogger(CompletionActions.class);

    /**
     * Logs the specified {@link Throwable}. For example:
     * <pre>{@code
     * CompletableFuture<?> f = ...;
     * f.exceptionally(CompletionActions::log);
     * }</pre>
     *
     * @return {@code null}
     */
    @Nullable
    public static <T> T log(Throwable cause) {
        logger.warn("Unexpected exception from a completion action:", cause);
        return null;
    }

    private CompletionActions() {}
}
