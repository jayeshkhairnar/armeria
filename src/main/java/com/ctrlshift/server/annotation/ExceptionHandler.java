package com.ctrlshift.server.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies an {@link ExceptionHandlerFunction} class which handles exceptions throwing from an
 * annotated service method.
 */
@Repeatable(ExceptionHandlers.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
public @interface ExceptionHandler {

    /**
     * {@link ExceptionHandlerFunction} implementation type. The specified class must have an accessible
     * default constructor.
     */
    Class<? extends ExceptionHandlerFunction> value();
}
