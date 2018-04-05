package com.ctrlshift.server.annotation;

import com.ctrlshift.commons.AggregatedHttpMessage;
import com.ctrlshift.internal.FallthroughException;
import com.ctrlshift.server.ServiceRequestContext;

/**
 * Converts an {@link AggregatedHttpMessage} to an object. The class implementing this interface would
 * be specified as a value of a {@link RequestConverter} annotation.
 *
 * @see RequestConverter
 * @see RequestObject
 */
@FunctionalInterface
public interface RequestConverterFunction {

    /**
     * Converts the specified {@code request} to an object of {@code expectedResultType}.
     * Calls {@link RequestConverterFunction#fallthrough()} or throws a {@link FallthroughException} if
     * this converter cannot convert the {@code request} to an object.
     */
    Object convertRequest(ServiceRequestContext ctx, AggregatedHttpMessage request,
                          Class<?> expectedResultType) throws Exception;

    /**
     * Throws a {@link FallthroughException} in order to try to convert the {@code request} to
     * an object by the next converter.
     */
    static <T> T fallthrough() {
        // Always throw the exception quietly.
        throw FallthroughException.get();
    }
}