package com.cigabyte.sitesentinel.engine.collection;

import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

public record ResponseBodyAnalysisResult(
        long byteLength,
        long characterLength,
        String sha256,
        Optional<String> bodySnippet,
        Optional<String> pageTitle,
        Optional<String> metaDescription,
        Optional<String> canonicalUrl
) {

    public ResponseBodyAnalysisResult {
        if (byteLength < 0) {
            throw new IllegalArgumentException(
                    "Response body byte length cannot be negative."
            );
        }

        if (characterLength < 0) {
            throw new IllegalArgumentException(
                    "Response body character length cannot be negative."
            );
        }

        sha256 = validateSha256(sha256);

        bodySnippet = normalizeOptional(
                bodySnippet,
                "Body snippet"
        );

        pageTitle = normalizeOptional(
                pageTitle,
                "Page title"
        );

        metaDescription = normalizeOptional(
                metaDescription,
                "Meta description"
        );

        canonicalUrl = normalizeOptional(
                canonicalUrl,
                "Canonical URL"
        );
    }

    private static String validateSha256(
            String sha256
    ) {
        Objects.requireNonNull(
                sha256,
                "Response body SHA-256 is required."
        );

        String normalizedValue =
                sha256.trim()
                        .toLowerCase(Locale.ROOT);

        if (!normalizedValue.matches(
                "[0-9a-f]{64}"
        )) {
            throw new IllegalArgumentException(
                    "Response body SHA-256 must contain "
                            + "64 hexadecimal characters."
            );
        }

        return normalizedValue;
    }

    private static Optional<String> normalizeOptional(
            Optional<String> value,
            String fieldName
    ) {
        Objects.requireNonNull(
                value,
                fieldName + " optional value is required."
        );

        return value
                .map(String::trim)
                .filter(item -> !item.isBlank());
    }
}