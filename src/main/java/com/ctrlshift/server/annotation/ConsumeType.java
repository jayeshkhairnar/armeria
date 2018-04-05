package com.ctrlshift.server.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies a media type which would be consumed by the service method or class.
 */
@Repeatable(ConsumeTypes.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
public @interface ConsumeType {

    /**
     * A media type string. For example,
     * <ul>
     *   <li>{@code application/json; charset=utf-8}</li>
     *   <li>{@code application/xml}</li>
     *   <li>{@code application/octet-stream}</li>
     *   <li>{@code text/html}</li>
     * </ul>
     */
    String value();
}
