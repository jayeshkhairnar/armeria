package com.ctrlshift.server.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.ctrlshift.commons.HttpRequest;
import com.ctrlshift.commons.HttpResponse;
import com.ctrlshift.server.DecoratingServiceFunction;

/**
 * Specifies a {@link DecoratingServiceFunction} class which handles an {@link HttpRequest} before invoking
 * an annotated service method.
 */
@Repeatable(Decorators.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
public @interface Decorator {

    /**
     * {@link DecoratingServiceFunction} implementation type. The specified class must have an accessible
     * default constructor.
     */
    Class<? extends DecoratingServiceFunction<HttpRequest, HttpResponse>> value();
}
