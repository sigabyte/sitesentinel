package com.cigabyte.sitesentinel.engine.collection.health;

import org.junit.jupiter.api.Test;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CrawlUrlNormalizerTests {

    private final CrawlUrlNormalizer normalizer =
            new CrawlUrlNormalizer();

    private final WebsiteHealthOrigin acceptedOrigin =
            WebsiteHealthOrigin.from(
                    URI.create(
                            "https://example.com/"
                    )
            );

    @Test
    void relativeReferenceIsResolvedAndNormalized() {
        CrawlUrlNormalizationResult result =
                normalizer.normalize(
                        URI.create(
                                "https://example.com/"
                                        + "products/category/"
                        ),
                        "../item/./details#specification",
                        acceptedOrigin
                );

        assertTrue(
                result.isAccepted()
        );

        assertEquals(
                CrawlUrlNormalizationStatus.ACCEPTED,
                result.status()
        );

        assertEquals(
                URI.create(
                        "https://example.com/"
                                + "products/item/details"
                ),
                result.normalizedUri()
                        .orElseThrow()
        );
    }

    @Test
    void fragmentIsRemovedAndQueryIsPreserved() {
        CrawlUrlNormalizationResult result =
                normalizer.normalize(
                        URI.create(
                                "https://example.com/"
                        ),
                        "/search?q=chair&page=2#results",
                        acceptedOrigin
                );

        assertTrue(
                result.isAccepted()
        );

        assertEquals(
                URI.create(
                        "https://example.com/"
                                + "search?q=chair&page=2"
                ),
                result.normalizedUri()
                        .orElseThrow()
        );
    }

    @Test
    void hostCaseAndExplicitDefaultPortAreCanonicalized() {
        CrawlUrlNormalizationResult result =
                normalizer.normalize(
                        URI.create(
                                "https://example.com/"
                        ),
                        "https://EXAMPLE.com:443/catalogue",
                        acceptedOrigin
                );

        assertTrue(
                result.isAccepted()
        );

        assertEquals(
                URI.create(
                        "https://example.com/catalogue"
                ),
                result.normalizedUri()
                        .orElseThrow()
        );
    }

    @Test
    void emptyPathIsCanonicalizedToRootPath() {
        CrawlUrlNormalizationResult result =
                normalizer.normalize(
                        URI.create(
                                "https://example.com/base"
                        ),
                        "https://example.com",
                        acceptedOrigin
                );

        assertTrue(
                result.isAccepted()
        );

        assertEquals(
                URI.create(
                        "https://example.com/"
                ),
                result.normalizedUri()
                        .orElseThrow()
        );
    }

    @Test
    void crossOriginReferenceIsNormalizedButNotAccepted() {
        CrawlUrlNormalizationResult result =
                normalizer.normalize(
                        URI.create(
                                "https://example.com/"
                        ),
                        "https://cdn.example.com/"
                                + "assets/app.js#version",
                        acceptedOrigin
                );

        assertFalse(
                result.isAccepted()
        );

        assertEquals(
                CrawlUrlNormalizationStatus.CROSS_ORIGIN,
                result.status()
        );

        assertEquals(
                URI.create(
                        "https://cdn.example.com/"
                                + "assets/app.js"
                ),
                result.normalizedUri()
                        .orElseThrow()
        );
    }

    @Test
    void nullAndBlankReferencesAreRejectedAsEmpty() {
        CrawlUrlNormalizationResult nullResult =
                normalizer.normalize(
                        URI.create(
                                "https://example.com/"
                        ),
                        null,
                        acceptedOrigin
                );

        CrawlUrlNormalizationResult blankResult =
                normalizer.normalize(
                        URI.create(
                                "https://example.com/"
                        ),
                        "   ",
                        acceptedOrigin
                );

        assertEquals(
                CrawlUrlNormalizationStatus.EMPTY_REFERENCE,
                nullResult.status()
        );

        assertEquals(
                CrawlUrlNormalizationStatus.EMPTY_REFERENCE,
                blankResult.status()
        );

        assertTrue(
                nullResult.normalizedUri()
                        .isEmpty()
        );

        assertTrue(
                blankResult.normalizedUri()
                        .isEmpty()
        );
    }

    @Test
    void malformedReferenceIsRejected() {
        CrawlUrlNormalizationResult result =
                normalizer.normalize(
                        URI.create(
                                "https://example.com/"
                        ),
                        "https://exa mple.com/page",
                        acceptedOrigin
                );

        assertFalse(
                result.isAccepted()
        );

        assertEquals(
                CrawlUrlNormalizationStatus
                        .MALFORMED_REFERENCE,
                result.status()
        );

        assertTrue(
                result.normalizedUri()
                        .isEmpty()
        );
    }

    @Test
    void unsupportedSchemesAreRejectedBeforeTransport() {
        String[] unsupportedReferences = {
                "javascript:alert(1)",
                "data:text/plain,content",
                "mailto:user@example.com",
                "tel:+123456789",
                "ftp://example.com/file",
                "file:///etc/passwd"
        };

        for (String reference
                : unsupportedReferences) {

            CrawlUrlNormalizationResult result =
                    normalizer.normalize(
                            URI.create(
                                    "https://example.com/"
                            ),
                            reference,
                            acceptedOrigin
                    );

            assertFalse(
                    result.isAccepted()
            );

            assertEquals(
                    CrawlUrlNormalizationStatus
                            .UNSUPPORTED_SCHEME,
                    result.status()
            );

            assertTrue(
                    result.normalizedUri()
                            .isEmpty()
            );
        }
    }

    @Test
    void embeddedCredentialsAreRejected() {
        CrawlUrlNormalizationResult result =
                normalizer.normalize(
                        URI.create(
                                "https://example.com/"
                        ),
                        "https://user:secret@example.com/page",
                        acceptedOrigin
                );

        assertFalse(
                result.isAccepted()
        );

        assertEquals(
                CrawlUrlNormalizationStatus
                        .EMBEDDED_CREDENTIALS,
                result.status()
        );

        assertTrue(
                result.normalizedUri()
                        .isEmpty()
        );
    }

    @Test
    void absoluteHttpReferenceWithoutHostIsRejected() {
        CrawlUrlNormalizationResult result =
                normalizer.normalize(
                        URI.create(
                                "https://example.com/"
                        ),
                        "https:/missing-host",
                        acceptedOrigin
                );

        assertFalse(
                result.isAccepted()
        );

        assertEquals(
                CrawlUrlNormalizationStatus.MISSING_HOST,
                result.status()
        );
    }

    @Test
    void schemeAndNonDefaultPortChangesAreCrossOrigin() {
        CrawlUrlNormalizationResult downgradeResult =
                normalizer.normalize(
                        URI.create(
                                "https://example.com/"
                        ),
                        "http://example.com/page",
                        acceptedOrigin
                );

        CrawlUrlNormalizationResult portResult =
                normalizer.normalize(
                        URI.create(
                                "https://example.com/"
                        ),
                        "https://example.com:8443/page",
                        acceptedOrigin
                );

        assertEquals(
                CrawlUrlNormalizationStatus.CROSS_ORIGIN,
                downgradeResult.status()
        );

        assertEquals(
                CrawlUrlNormalizationStatus.CROSS_ORIGIN,
                portResult.status()
        );

        assertTrue(
                downgradeResult.normalizedUri()
                        .isPresent()
        );

        assertTrue(
                portResult.normalizedUri()
                        .isPresent()
        );
    }
}