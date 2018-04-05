package com.ctrlshift.server.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies which parameter should be converted by {@link RequestConverterFunction}.
 *
 * @see RequestConverterFunction
 * @see RequestConverter
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface RequestObject {

    /**
     * {@link RequestConverterFunction} implementation type which is used for converting the annotated
     * parameter. The specified class must have an accessible default constructor.
     */
    Class<? extends RequestConverterFunction> value() default RequestConverterFunction.class;
}
