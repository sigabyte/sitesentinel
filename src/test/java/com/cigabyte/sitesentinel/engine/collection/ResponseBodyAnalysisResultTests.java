package com.cigabyte.sitesentinel.engine.collection;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ResponseBodyAnalysisResultTests {

    private static final String VALID_SHA256 =
            "0123456789abcdef"
                    + "0123456789abcdef"
                    + "0123456789abcdef"
                    + "0123456789abcdef";

    @Test
    void validResultPreservesLengthsAndNormalizesValues() {
        ResponseBodyAnalysisResult result =
                new ResponseBodyAnalysisResult(
                        4096,
                        3900,
                        VALID_SHA256.toUpperCase(),
                        Optional.of(
                                "  SiteSentinel body snippet  "
                        ),
                        Optional.of(
                                "  Example Page  "
                        ),
                        Optional.of(
                                "  Example description  "
                        ),
                        Optional.of(
                                "  https://example.com/  "
                        )
                );

        assertEquals(
                4096,
                result.byteLength()
        );

        assertEquals(
                3900,
                result.characterLength()
        );

        assertEquals(
                VALID_SHA256,
                result.sha256()
        );

        assertEquals(
                Optional.of(
                        "SiteSentinel body snippet"
                ),
                result.bodySnippet()
        );

        assertEquals(
                Optional.of(
                        "Example Page"
                ),
                result.pageTitle()
        );

        assertEquals(
                Optional.of(
                        "Example description"
                ),
                result.metaDescription()
        );

        assertEquals(
                Optional.of(
                        "https://example.com/"
                ),
                result.canonicalUrl()
        );
    }

    @Test
    void blankOptionalValuesAreNormalizedToEmpty() {
        ResponseBodyAnalysisResult result =
                new ResponseBodyAnalysisResult(
                        0,
                        0,
                        VALID_SHA256,
                        Optional.of("   "),
                        Optional.of("\t"),
                        Optional.of("\n"),
                        Optional.of("")
                );

        assertTrue(
                result.bodySnippet().isEmpty()
        );

        assertTrue(
                result.pageTitle().isEmpty()
        );

        assertTrue(
                result.metaDescription().isEmpty()
        );

        assertTrue(
                result.canonicalUrl().isEmpty()
        );
    }

    @Test
    void negativeLengthsAreRejected() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new ResponseBodyAnalysisResult(
                        -1,
                        0,
                        VALID_SHA256,
                        Optional.empty(),
                        Optional.empty(),
                        Optional.empty(),
                        Optional.empty()
                )
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> new ResponseBodyAnalysisResult(
                        0,
                        -1,
                        VALID_SHA256,
                        Optional.empty(),
                        Optional.empty(),
                        Optional.empty(),
                        Optional.empty()
                )
        );
    }

    @Test
    void invalidSha256ValuesAreRejected() {
        assertThrows(
                NullPointerException.class,
                () -> new ResponseBodyAnalysisResult(
                        0,
                        0,
                        null,
                        Optional.empty(),
                        Optional.empty(),
                        Optional.empty(),
                        Optional.empty()
                )
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> new ResponseBodyAnalysisResult(
                        0,
                        0,
                        "abc123",
                        Optional.empty(),
                        Optional.empty(),
                        Optional.empty(),
                        Optional.empty()
                )
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> new ResponseBodyAnalysisResult(
                        0,
                        0,
                        "z".repeat(64),
                        Optional.empty(),
                        Optional.empty(),
                        Optional.empty(),
                        Optional.empty()
                )
        );
    }

    @Test
    void nullOptionalContainersAreRejected() {
        assertThrows(
                NullPointerException.class,
                () -> new ResponseBodyAnalysisResult(
                        0,
                        0,
                        VALID_SHA256,
                        null,
                        Optional.empty(),
                        Optional.empty(),
                        Optional.empty()
                )
        );

        assertThrows(
                NullPointerException.class,
                () -> new ResponseBodyAnalysisResult(
                        0,
                        0,
                        VALID_SHA256,
                        Optional.empty(),
                        null,
                        Optional.empty(),
                        Optional.empty()
                )
        );

        assertThrows(
                NullPointerException.class,
                () -> new ResponseBodyAnalysisResult(
                        0,
                        0,
                        VALID_SHA256,
                        Optional.empty(),
                        Optional.empty(),
                        null,
                        Optional.empty()
                )
        );

        assertThrows(
                NullPointerException.class,
                () -> new ResponseBodyAnalysisResult(
                        0,
                        0,
                        VALID_SHA256,
                        Optional.empty(),
                        Optional.empty(),
                        Optional.empty(),
                        null
                )
        );
    }
}