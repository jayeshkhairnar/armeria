package com.ctrlshift.client;

import com.ctrlshift.commons.util.Version;

import io.netty.util.AsciiString;

final class HttpHeaderUtil {

    private static final String CLIENT_ARTIFACT_ID = "armeria";

    static final AsciiString USER_AGENT = AsciiString.of(createUserAgentName());

    static String hostHeader(String host, int port, int defaultPort) {
        if (port == defaultPort) {
            return host;
        }

        return new StringBuilder(host.length() + 6).append(host).append(':').append(port).toString();
    }

    private static String createUserAgentName() {
        final Version version = Version.identify(HttpHeaderUtil.class.getClassLoader())
                                       .get(CLIENT_ARTIFACT_ID);

        return CLIENT_ARTIFACT_ID + '/' + (version != null ? version.artifactVersion() : "unknown");
    }

    private HttpHeaderUtil() {}
}