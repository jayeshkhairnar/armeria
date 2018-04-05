/*
 * Copyright 2016 LINE Corporation
 *
 * LINE Corporation licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package com.ctrlshift.client;

import java.time.Duration;

import javax.annotation.Nullable;

import com.ctrlshift.commons.ContentTooLargeException;
import com.ctrlshift.commons.HttpHeaders;
import com.ctrlshift.commons.HttpRequest;
import com.ctrlshift.commons.Request;
import com.ctrlshift.commons.RequestContext;
import com.ctrlshift.commons.Response;

import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

/**
 * Provides information about a {@link Request}, its {@link Response} and its related utilities.
 * Every client request has its own {@link ClientRequestContext} instance.
 */
public interface ClientRequestContext extends RequestContext {

    @Override
    ClientRequestContext newDerivedContext();

    @Override
    ClientRequestContext newDerivedContext(Request request);

    
    AttributeKey<HttpHeaders> HTTP_HEADERS = AttributeKey.valueOf(ClientRequestContext.class, "HTTP_HEADERS");

    /**
     * Returns the remote {@link Endpoint} of the current {@link Request}.
     */
    Endpoint endpoint();

    /**
     * Returns the {@link ClientOptions} of the current {@link Request}.
     */
    //ClientOptions options();

    /**
     * Returns the fragment part of the URI of the current {@link Request}, as defined in
     * <a href="https://tools.ietf.org/html/rfc3986#section-3.5">the section 3.5 of RFC3986</a>.
     *
     * @return the fragment part of the request URI, or {@code null} if no fragment was specified
     */
    @Nullable
    String fragment();

    long writeTimeoutMillis();

    void setWriteTimeoutMillis(long writeTimeoutMillis);

    void setWriteTimeout(Duration writeTimeout);

    long responseTimeoutMillis();

    void setResponseTimeoutMillis(long responseTimeoutMillis);

    
    void setResponseTimeout(Duration responseTimeout);

   
    long maxResponseLength();

   
    void setMaxResponseLength(long maxResponseLength);
}