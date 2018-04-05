package com.ctrlshift.commons.util;

import java.io.InputStream;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.annotation.Nullable;

import com.google.common.io.Closeables;

/**
 * Retrieves the version information of available Armeria artifacts.
 *
 * <p>This class retrieves the version information from
 * {@code META-INF/com.linecorp.armeria.versions.properties}, which is generated in build time. Note that
 * it may not be possible to retrieve the information completely, depending on your environment, such as
 * the specified {@link ClassLoader}, the current {@link SecurityManager}.
 */
public final class Version {

    // Forked from Netty at d0912f27091e4548466df81f545c017a25c9d256

    private static final String PROP_RESOURCE_PATH = "META-INF/com.linecorp.armeria.versions.properties";

    private static final String PROP_VERSION = ".version";
    private static final String PROP_COMMIT_DATE = ".commitDate";
    private static final String PROP_SHORT_COMMIT_HASH = ".shortCommitHash";
    private static final String PROP_LONG_COMMIT_HASH = ".longCommitHash";
    private static final String PROP_REPO_STATUS = ".repoStatus";

    /**
     * Retrieves the version information of Armeria artifacts using the current
     * {@linkplain Thread#getContextClassLoader() context class loader}.
     *
     * @return A {@link Map} whose keys are Maven artifact IDs and whose values are {@link Version}s
     */
    public static Map<String, Version> identify() {
        return identify(null);
    }

    /**
     * Retrieves the version information of Armeria artifacts using the specified {@link ClassLoader}.
     *
     * @return A {@link Map} whose keys are Maven artifact IDs and whose values are {@link Version}s
     */
    public static Map<String, Version> identify(@Nullable ClassLoader classLoader) {
        if (classLoader == null) {
            classLoader = getContextClassLoader();
        }

        // Collect all properties.
        final Properties props = new Properties();
        try {
            final Enumeration<URL> resources = classLoader.getResources(PROP_RESOURCE_PATH);
            while (resources.hasMoreElements()) {
                final URL url = resources.nextElement();
                final InputStream in = url.openStream();
                try {
                    props.load(in);
                } finally {
                    Closeables.closeQuietly(in);
                }
            }
        } catch (Exception ignore) {
            // Not critical. Just ignore.
        }

        // Collect all artifactIds.
        final Set<String> artifactIds = new HashSet<>();
        for (Object o: props.keySet()) {
            final String k = (String) o;

            final int dotIndex = k.indexOf('.');
            if (dotIndex <= 0) {
                continue;
            }

            final String artifactId = k.substring(0, dotIndex);

            // Skip the entries without required information.
            if (!props.containsKey(artifactId + PROP_VERSION) ||
                !props.containsKey(artifactId + PROP_COMMIT_DATE) ||
                !props.containsKey(artifactId + PROP_SHORT_COMMIT_HASH) ||
                !props.containsKey(artifactId + PROP_LONG_COMMIT_HASH) ||
                !props.containsKey(artifactId + PROP_REPO_STATUS)) {
                continue;
            }

            artifactIds.add(artifactId);
        }

        final Map<String, Version> versions = new HashMap<>();
        for (String artifactId: artifactIds) {
            versions.put(
                    artifactId,
                    new Version(
                            artifactId,
                            props.getProperty(artifactId + PROP_VERSION),
                            parseIso8601(props.getProperty(artifactId + PROP_COMMIT_DATE)),
                            props.getProperty(artifactId + PROP_SHORT_COMMIT_HASH),
                            props.getProperty(artifactId + PROP_LONG_COMMIT_HASH),
                            props.getProperty(artifactId + PROP_REPO_STATUS)));
        }

        return versions;
    }

    private static ClassLoader getContextClassLoader() {
        if (System.getSecurityManager() == null) {
            return Thread.currentThread().getContextClassLoader();
        } else {
            return AccessController.doPrivileged(
                    (PrivilegedAction<ClassLoader>) () -> Thread.currentThread().getContextClassLoader());
        }
    }

    private static long parseIso8601(String value) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z").parse(value).getTime();
        } catch (ParseException ignored) {
            return 0;
        }
    }

    private final String artifactId;
    private final String artifactVersion;
    private final long commitTimeMillis;
    private final String shortCommitHash;
    private final String longCommitHash;
    private final String repositoryStatus;

    private Version(
            String artifactId, String artifactVersion, long commitTimeMillis,
            String shortCommitHash, String longCommitHash, String repositoryStatus) {
        this.artifactId = artifactId;
        this.artifactVersion = artifactVersion;
        this.commitTimeMillis = commitTimeMillis;
        this.shortCommitHash = shortCommitHash;
        this.longCommitHash = longCommitHash;
        this.repositoryStatus = repositoryStatus;
    }

    public String artifactId() {
        return artifactId;
    }

    public String artifactVersion() {
        return artifactVersion;
    }

    public long commitTimeMillis() {
        return commitTimeMillis;
    }

    public String shortCommitHash() {
        return shortCommitHash;
    }

    public String longCommitHash() {
        return longCommitHash;
    }

    public String repositoryStatus() {
        return repositoryStatus;
    }

    @Override
    public String toString() {
        return artifactId + '-' + artifactVersion + '.' + shortCommitHash +
                (isClean() ? "" : "(repository: " + repositoryStatus + ')');
    }

    /**
     * Returns true if repository status is not dirty.
     */
    public boolean isClean() {
        return "clean".equals(repositoryStatus);
    }
}
