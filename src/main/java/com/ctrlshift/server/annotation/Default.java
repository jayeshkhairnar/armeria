package com.ctrlshift.server.annotation;

import static com.ctrlshift.internal.DefaultValues.UNSPECIFIED;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies the default value of an optional parameter.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Default {

    /**
     * The default value to use as a fallback when the request parameter is not provided or has an empty value.
     * When {@link Default} annotation exists but {@link Default#value()} is not specified, {@code null}
     * value would be set if the parameter is not present in the request.
     *
     * {@link Default} annotation is not allowed for a path variable. If a user uses {@link Default}
     * annotation on a path variable, {@link IllegalArgumentException} would be raised.
     */
    String value() default UNSPECIFIED;
}