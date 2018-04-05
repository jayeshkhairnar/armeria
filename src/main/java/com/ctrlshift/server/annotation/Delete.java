package com.ctrlshift.server.annotation;

import static com.ctrlshift.internal.DefaultValues.UNSPECIFIED;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.ctrlshift.commons.HttpMethod;

/**
 * Annotation for mapping {@link HttpMethod#DELETE} onto specific method.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Delete {

    /**
     * A path pattern for the annotated method.
     */
    String value() default UNSPECIFIED;
}