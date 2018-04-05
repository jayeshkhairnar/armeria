package com.ctrlshift.server.encoding;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPOutputStream;

import javax.annotation.Nullable;

import com.ctrlshift.commons.HttpHeaderNames;
import com.ctrlshift.commons.HttpRequest;

/**
 * Support utilities for dealing with HTTP encoding (e.g., gzip).
 */
final class HttpEncoders {

    @Nullable
    static HttpEncodingType getWrapperForRequest(HttpRequest request) {
        final String acceptEncoding = request.headers().get(HttpHeaderNames.ACCEPT_ENCODING);
        if (acceptEncoding == null) {
            return null;
        }
        return determineEncoding(acceptEncoding);
    }

    static DeflaterOutputStream getEncodingOutputStream(HttpEncodingType encodingType, OutputStream out) {
        switch (encodingType) {
            case GZIP:
                try {
                    return new GZIPOutputStream(out, true);
                } catch (IOException e) {
                    throw new IllegalStateException(
                            "Error writing gzip header. This should not happen with byte arrays.", e);
                }
            case DEFLATE:
                return new DeflaterOutputStream(out, true);
            default:
                throw new IllegalArgumentException("Unexpected zlib type, this is a programming bug.");
        }
    }

    // Copied from netty's HttpContentCompressor.
    @Nullable
    private static HttpEncodingType determineEncoding(String acceptEncoding) {
        float starQ = -1.0f;
        float gzipQ = -1.0f;
        float deflateQ = -1.0f;
        for (String encoding : acceptEncoding.split(",")) {
            float q = 1.0f;
            final int equalsPos = encoding.indexOf('=');
            if (equalsPos != -1) {
                try {
                    q = Float.parseFloat(encoding.substring(equalsPos + 1));
                } catch (NumberFormatException e) {
                    // Ignore encoding
                    q = 0.0f;
                }
            }
            if (encoding.contains("*")) {
                starQ = q;
            } else if (encoding.contains("gzip") && q > gzipQ) {
                gzipQ = q;
            } else if (encoding.contains("deflate") && q > deflateQ) {
                deflateQ = q;
            }
        }
        if (gzipQ > 0.0f || deflateQ > 0.0f) {
            if (gzipQ >= deflateQ) {
                return HttpEncodingType.GZIP;
            } else {
                return HttpEncodingType.DEFLATE;
            }
        }
        if (starQ > 0.0f) {
            if (gzipQ == -1.0f) {
                return HttpEncodingType.GZIP;
            }
            if (deflateQ == -1.0f) {
                return HttpEncodingType.DEFLATE;
            }
        }
        return null;
    }

    private HttpEncoders() {}
}