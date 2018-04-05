package com.ctrlshift.server.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The containing annotation type for {@link Decorator}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Decorators {
    /**
     * An array of {@link Decorator}s.
     */
    Decorator[] value();
}
