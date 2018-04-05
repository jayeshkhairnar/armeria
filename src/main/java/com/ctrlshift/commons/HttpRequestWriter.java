package com.ctrlshift.commons;

import com.ctrlshift.commons.stream.StreamWriter;

/**
 * An {@link HttpRequest} that can have {@link HttpObject}s written to it.
 * Use {@link HttpRequest#streaming(HttpHeaders)} to construct.
 */
public interface HttpRequestWriter extends HttpRequest, StreamWriter<HttpObject> {
}
