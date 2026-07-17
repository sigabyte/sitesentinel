package com.cigabyte.sitesentinel.recommendation;

import java.util.regex.Pattern;

public record RiskRemediationAiRequest(
        String promptVersion,
        String outputSchemaVersion,
        String systemInstruction,
        String userInstruction,
        String contextFingerprint
) {

    private static final Pattern SHA_256_PATTERN =
            Pattern.compile("[0-9a-f]{64}");

    public RiskRemediationAiRequest {
        promptVersion = requireText(
                promptVersion,
                "Prompt version"
        );

        outputSchemaVersion = requireText(
                outputSchemaVersion,
                "Output schema version"
        );

        systemInstruction = requireText(
                systemInstruction,
                "System instruction"
        );

        userInstruction = requireText(
                userInstruction,
                "User instruction"
        );

        contextFingerprint = requireFingerprint(
                contextFingerprint
        );
    }

    private static String requireText(
            String value,
            String fieldName
    ) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    fieldName + " is required."
            );
        }

        return value.trim();
    }

    private static String requireFingerprint(
            String value
    ) {
        String normalizedValue = requireText(
                value,
                "Context fingerprint"
        );

        if (!SHA_256_PATTERN
                .matcher(normalizedValue)
                .matches()) {

            throw new IllegalArgumentException(
                    "Context fingerprint must be a lowercase "
                            + "64-character SHA-256 hexadecimal value."
            );
        }

        return normalizedValue;
    }
}