package com.cigabyte.sitesentinel.engine.collection.health;

import java.net.IDN;
import java.net.URI;
import java.util.Locale;
import java.util.Objects;

public record WebsiteHealthOrigin(
        String scheme,
        String host,
        int port
) {

    public WebsiteHealthOrigin {
        scheme =
                normalizeScheme(
                        scheme
                );

        host =
                normalizeHost(
                        host
                );

        if (port < 1 || port > 65535) {
            throw new IllegalArgumentException(
                    "Origin port must be between "
                            + "1 and 65535."
            );
        }
    }

    public static WebsiteHealthOrigin from(
            URI uri
    ) {
        Objects.requireNonNull(
                uri,
                "Origin URI is required."
        );

        if (uri.isOpaque()) {
            throw new IllegalArgumentException(
                    "Origin URI must be hierarchical."
            );
        }

        if (uri.getRawUserInfo() != null) {
            throw new IllegalArgumentException(
                    "Origin URI must not contain "
                            + "embedded credentials."
            );
        }

        String normalizedScheme =
                normalizeScheme(
                        uri.getScheme()
                );

        String normalizedHost =
                normalizeHost(
                        uri.getHost()
                );

        int effectivePort =
                resolveEffectivePort(
                        normalizedScheme,
                        uri.getPort()
                );

        return new WebsiteHealthOrigin(
                normalizedScheme,
                normalizedHost,
                effectivePort
        );
    }

    private static String normalizeScheme(
            String scheme
    ) {
        if (scheme == null
                || scheme.isBlank()) {

            throw new IllegalArgumentException(
                    "Origin URI scheme is required."
            );
        }

        String normalizedScheme =
                scheme.trim()
                        .toLowerCase(
                                Locale.ROOT
                        );

        if (!"http".equals(
                normalizedScheme
        )
                && !"https".equals(
                normalizedScheme
        )) {

            throw new IllegalArgumentException(
                    "Only HTTP and HTTPS origins "
                            + "are supported."
            );
        }

        return normalizedScheme;
    }

    private static String normalizeHost(
            String host
    ) {
        if (host == null
                || host.isBlank()) {

            throw new IllegalArgumentException(
                    "Origin URI host is required."
            );
        }

        String normalizedHost =
                host.trim()
                        .toLowerCase(
                                Locale.ROOT
                        );

        if (normalizedHost.startsWith("[")
                && normalizedHost.endsWith("]")) {

            normalizedHost =
                    normalizedHost.substring(
                            1,
                            normalizedHost.length() - 1
                    );
        }

        while (normalizedHost.endsWith(".")) {
            normalizedHost =
                    normalizedHost.substring(
                            0,
                            normalizedHost.length() - 1
                    );
        }

        if (normalizedHost.isBlank()) {
            throw new IllegalArgumentException(
                    "Origin URI host is required."
            );
        }

        if (!normalizedHost.contains(":")) {
            normalizedHost =
                    IDN.toASCII(
                                    normalizedHost,
                                    IDN.USE_STD3_ASCII_RULES
                            )
                            .toLowerCase(
                                    Locale.ROOT
                            );
        }

        return normalizedHost;
    }

    private static int resolveEffectivePort(
            String scheme,
            int configuredPort
    ) {
        if (configuredPort == -1) {
            return "https".equals(
                    scheme
            )
                    ? 443
                    : 80;
        }

        if (configuredPort < 1
                || configuredPort > 65535) {

            throw new IllegalArgumentException(
                    "Origin URI port must be between "
                            + "1 and 65535."
            );
        }

        return configuredPort;
    }
}