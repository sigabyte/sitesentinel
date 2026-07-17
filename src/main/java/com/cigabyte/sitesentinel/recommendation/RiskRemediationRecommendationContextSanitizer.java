package com.cigabyte.sitesentinel.recommendation;

import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class RiskRemediationRecommendationContextSanitizer {

    private static final String REDACTED =
            "[REDACTED]";

    private static final String TRUNCATED_SUFFIX =
            " [TRUNCATED]";

    private static final Pattern PRIVATE_KEY_PATTERN =
            Pattern.compile(
                    "-----BEGIN(?: [A-Z0-9]+)* PRIVATE KEY-----"
                            + ".*?"
                            + "-----END(?: [A-Z0-9]+)* PRIVATE KEY-----",
                    Pattern.CASE_INSENSITIVE
                            | Pattern.DOTALL
            );

    private static final Pattern AUTHORIZATION_PATTERN =
            Pattern.compile(
                    "\\b(Bearer|Basic)\\s+"
                            + "[A-Za-z0-9._~+/=-]+",
                    Pattern.CASE_INSENSITIVE
            );

    private static final Pattern TELEGRAM_TOKEN_PATTERN =
            Pattern.compile(
                    "\\b\\d{6,12}:"
                            + "[A-Za-z0-9_-]{20,}\\b"
            );

    private static final Pattern JWT_PATTERN =
            Pattern.compile(
                    "\\beyJ[A-Za-z0-9_-]{8,}"
                            + "\\.[A-Za-z0-9_-]{8,}"
                            + "\\.[A-Za-z0-9_-]{8,}\\b"
            );

    private static final Pattern URI_USER_INFO_PATTERN =
            Pattern.compile(
                    "(https?://)"
                            + "[^\\s/@:]+:"
                            + "[^\\s/@]+@",
                    Pattern.CASE_INSENSITIVE
            );

    private static final Pattern SENSITIVE_ASSIGNMENT_PATTERN =
            Pattern.compile(
                    "\\b("
                            + "[A-Z0-9_.-]*"
                            + "(?:"
                            + "API[_-]?KEY"
                            + "|ACCESS[_-]?TOKEN"
                            + "|REFRESH[_-]?TOKEN"
                            + "|BOT[_-]?TOKEN"
                            + "|TOKEN"
                            + "|SECRET"
                            + "|PASSWORD"
                            + "|PASSWD"
                            + "|AUTHORIZATION"
                            + "|COOKIE"
                            + "|SESSION[_-]?ID"
                            + "|CHAT[_-]?ID"
                            + ")"
                            + "[A-Z0-9_.-]*"
                            + ")"
                            + "\\s*[:=]\\s*"
                            + "(\"[^\"]*\""
                            + "|'[^']*'"
                            + "|[^\\s,;]+)",
                    Pattern.CASE_INSENSITIVE
            );

    private static final Pattern WHITESPACE_PATTERN =
            Pattern.compile("\\s+");

    public String sanitizeRiskType(String value) {
        return sanitizeRequired(
                value,
                "Risk type",
                100
        );
    }

    public String sanitizeRiskRationale(String value) {
        return sanitizeRequired(
                value,
                "Risk rationale",
                3000
        );
    }

    public String sanitizeFindingType(String value) {
        return sanitizeRequired(
                value,
                "Finding type",
                100
        );
    }

    public String sanitizeFindingTitle(String value) {
        return sanitizeRequired(
                value,
                "Finding title",
                220
        );
    }

    public String sanitizeFindingDescription(String value) {
        return sanitizeRequired(
                value,
                "Finding description",
                2000
        );
    }

    public String sanitizeNormalizedEvidenceType(
            String value
    ) {
        return sanitizeRequired(
                value,
                "Normalized evidence type",
                80
        );
    }

    public String sanitizeNormalizedEvidenceValue(
            String value
    ) {
        return sanitizeRequired(
                value,
                "Normalized evidence value",
                1200
        );
    }

    public boolean containsSensitiveMaterial(
            String value
    ) {
        if (value == null || value.isBlank()) {
            return false;
        }

        return PRIVATE_KEY_PATTERN
                .matcher(value)
                .find()
                || AUTHORIZATION_PATTERN
                .matcher(value)
                .find()
                || TELEGRAM_TOKEN_PATTERN
                .matcher(value)
                .find()
                || JWT_PATTERN
                .matcher(value)
                .find()
                || URI_USER_INFO_PATTERN
                .matcher(value)
                .find()
                || SENSITIVE_ASSIGNMENT_PATTERN
                .matcher(value)
                .find();
    }

    private String sanitizeRequired(
            String value,
            String fieldName,
            int maximumLength
    ) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    fieldName + " is required."
            );
        }

        String sanitized =
                PRIVATE_KEY_PATTERN
                        .matcher(value)
                        .replaceAll(REDACTED);

        sanitized =
                AUTHORIZATION_PATTERN
                        .matcher(sanitized)
                        .replaceAll("$1 " + REDACTED);

        sanitized =
                TELEGRAM_TOKEN_PATTERN
                        .matcher(sanitized)
                        .replaceAll(REDACTED);

        sanitized =
                JWT_PATTERN
                        .matcher(sanitized)
                        .replaceAll(REDACTED);

        sanitized =
                URI_USER_INFO_PATTERN
                        .matcher(sanitized)
                        .replaceAll(
                                "$1" + REDACTED + "@"
                        );

        sanitized =
                SENSITIVE_ASSIGNMENT_PATTERN
                        .matcher(sanitized)
                        .replaceAll(
                                "$1=" + REDACTED
                        );

        sanitized =
                WHITESPACE_PATTERN
                        .matcher(sanitized)
                        .replaceAll(" ")
                        .trim();

        if (sanitized.isBlank()) {
            throw new IllegalArgumentException(
                    fieldName
                            + " is empty after sanitization."
            );
        }

        return truncateUtf16Safe(
                sanitized,
                maximumLength
        );
    }

    private String truncateUtf16Safe(
            String value,
            int maximumLength
    ) {
        if (value.length() <= maximumLength) {
            return value;
        }

        int contentLimit =
                maximumLength
                        - TRUNCATED_SUFFIX.length();

        int endIndex = contentLimit;

        if (endIndex > 0
                && Character.isHighSurrogate(
                value.charAt(endIndex - 1)
        )) {
            endIndex--;
        }

        return value.substring(0, endIndex)
                .stripTrailing()
                + TRUNCATED_SUFFIX;
    }
}