package com.cigabyte.sitesentinel.engine.collection.health;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;
import java.util.Objects;

public class CrawlUrlNormalizer {

    public CrawlUrlNormalizationResult normalize(
            URI baseUri,
            String rawReference,
            WebsiteHealthOrigin acceptedOrigin
    ) {
        Objects.requireNonNull(
                baseUri,
                "Base URI is required."
        );

        Objects.requireNonNull(
                acceptedOrigin,
                "Accepted origin is required."
        );

        if (rawReference == null
                || rawReference.isBlank()) {

            return CrawlUrlNormalizationResult.rejected(
                    CrawlUrlNormalizationStatus
                            .EMPTY_REFERENCE
            );
        }

        URI resolvedUri;

        try {
            URI reference =
                    new URI(
                            rawReference.trim()
                    );

            resolvedUri =
                    baseUri.resolve(
                                    reference
                            )
                            .normalize();
        } catch (URISyntaxException
                 | IllegalArgumentException exception) {

            return CrawlUrlNormalizationResult.rejected(
                    CrawlUrlNormalizationStatus
                            .MALFORMED_REFERENCE
            );
        }

        String scheme =
                resolvedUri.getScheme();

        if (scheme == null
                || (!"http".equalsIgnoreCase(
                scheme
        )
                && !"https".equalsIgnoreCase(
                scheme
        ))) {

            return CrawlUrlNormalizationResult.rejected(
                    CrawlUrlNormalizationStatus
                            .UNSUPPORTED_SCHEME
            );
        }

        if (resolvedUri.getRawUserInfo() != null) {
            return CrawlUrlNormalizationResult.rejected(
                    CrawlUrlNormalizationStatus
                            .EMBEDDED_CREDENTIALS
            );
        }

        if (resolvedUri.getHost() == null
                || resolvedUri.getHost()
                .isBlank()) {

            return CrawlUrlNormalizationResult.rejected(
                    CrawlUrlNormalizationStatus
                            .MISSING_HOST
            );
        }

        URI normalizedUri;

        try {
            normalizedUri =
                    buildCanonicalUri(
                            resolvedUri
                    );
        } catch (IllegalArgumentException exception) {
            return CrawlUrlNormalizationResult.rejected(
                    CrawlUrlNormalizationStatus
                            .MALFORMED_REFERENCE
            );
        }

        WebsiteHealthOrigin targetOrigin;

        try {
            targetOrigin =
                    WebsiteHealthOrigin.from(
                            normalizedUri
                    );
        } catch (IllegalArgumentException exception) {
            return CrawlUrlNormalizationResult.rejected(
                    CrawlUrlNormalizationStatus
                            .MALFORMED_REFERENCE
            );
        }

        if (!acceptedOrigin.equals(
                targetOrigin
        )) {
            return CrawlUrlNormalizationResult.rejected(
                    CrawlUrlNormalizationStatus
                            .CROSS_ORIGIN,
                    normalizedUri
            );
        }

        return CrawlUrlNormalizationResult.accepted(
                normalizedUri
        );
    }

    private URI buildCanonicalUri(
            URI uri
    ) {
        WebsiteHealthOrigin origin =
                WebsiteHealthOrigin.from(
                        uri
                );

        String scheme =
                origin.scheme()
                        .toLowerCase(
                                Locale.ROOT
                        );

        String host =
                origin.host();

        String authorityHost =
                host.contains(":")
                        ? "[" + host + "]"
                        : host;

        boolean defaultPort =
                ("https".equals(scheme)
                        && origin.port() == 443)
                        || ("http".equals(scheme)
                        && origin.port() == 80);

        String authority =
                authorityHost
                        + (defaultPort
                        ? ""
                        : ":" + origin.port());

        String rawPath =
                uri.getRawPath();

        if (rawPath == null
                || rawPath.isBlank()) {

            rawPath = "/";
        }

        String rawQuery =
                uri.getRawQuery();

        String value =
                scheme
                        + "://"
                        + authority
                        + rawPath
                        + (rawQuery == null
                        ? ""
                        : "?" + rawQuery);

        return URI.create(
                        value
                )
                .normalize();
    }
}