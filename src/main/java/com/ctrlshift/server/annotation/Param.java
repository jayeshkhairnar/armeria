package com.ctrlshift.server.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for mapping a parameter of a request onto a method parameter.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Param {

    /**
     * The name of the request parameter to bind to.
     * The path variable, the parameter name in a query string or a URL-encoded form data,
     * or the name of a multipart.
     */
    String value();
}