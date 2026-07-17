package com.cigabyte.sitesentinel.recommendation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RiskRemediationRecommendationContextSanitizerTests {

    private static final String TELEGRAM_TOKEN =
            "123456789:ABCDEFGHIJKLMNOPQRSTUVWXYZ_1234";

    private static final String PASSWORD =
            "super-secret-password";

    private final RiskRemediationRecommendationContextSanitizer
            sanitizer =
            new RiskRemediationRecommendationContextSanitizer();

    @Test
    void sanitizesSensitiveMaterialBeforePromptContextUse() {
        String input = """
                Authorization: Bearer sensitive-access-token
                PASSWORD=%s
                BOT_TOKEN=%s
                endpoint=https://user:private-password@example.test/path
                """.formatted(
                PASSWORD,
                TELEGRAM_TOKEN
        );

        String sanitized =
                sanitizer.sanitizeRiskRationale(input);

        assertFalse(
                sanitized.contains(
                        "sensitive-access-token"
                )
        );

        assertFalse(
                sanitized.contains(PASSWORD)
        );

        assertFalse(
                sanitized.contains(TELEGRAM_TOKEN)
        );

        assertFalse(
                sanitized.contains(
                        "user:private-password@"
                )
        );

        assertTrue(
                sanitized.contains("[REDACTED]")
        );
    }

    @Test
    void sanitizesPrivateKeyBlocks() {
        String privateKey = """
                Configuration failure:
                -----BEGIN PRIVATE KEY-----
                PRIVATE_KEY_CONTENT_MUST_NOT_REACH_PROMPT
                -----END PRIVATE KEY-----
                """;

        String sanitized =
                sanitizer.sanitizeFindingDescription(
                        privateKey
                );

        assertFalse(
                sanitized.contains(
                        "PRIVATE_KEY_CONTENT_MUST_NOT_REACH_PROMPT"
                )
        );

        assertTrue(
                sanitized.contains("[REDACTED]")
        );
    }

    @Test
    void truncatesUtf16TextWithoutSplittingSurrogatePairs() {
        String value =
                "a".repeat(1187)
                        + "😀"
                        + "b".repeat(100);

        String sanitized =
                sanitizer
                        .sanitizeNormalizedEvidenceValue(
                                value
                        );

        assertTrue(sanitized.length() <= 1200);

        assertTrue(
                sanitized.endsWith(
                        " [TRUNCATED]"
                )
        );

        assertNoUnpairedSurrogates(sanitized);
    }

    @Test
    void rejectsBlankRequiredContextText() {
        assertThrows(
                IllegalArgumentException.class,
                () -> sanitizer.sanitizeRiskRationale(
                        "   "
                )
        );
    }

    private void assertNoUnpairedSurrogates(
            String value
    ) {
        for (int index = 0;
             index < value.length();
             index++) {

            char current = value.charAt(index);

            if (Character.isHighSurrogate(current)) {
                assertTrue(
                        index + 1 < value.length(),
                        "High surrogate must have a following character."
                );

                assertTrue(
                        Character.isLowSurrogate(
                                value.charAt(index + 1)
                        ),
                        "High surrogate must be followed by a low surrogate."
                );

                index++;
                continue;
            }

            assertFalse(
                    Character.isLowSurrogate(current),
                    "Low surrogate must not appear without a high surrogate."
            );
        }
    }
}