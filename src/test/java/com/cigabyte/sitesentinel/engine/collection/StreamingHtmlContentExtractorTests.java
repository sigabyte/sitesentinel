package com.cigabyte.sitesentinel.engine.collection;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.net.URI;
import java.net.http.HttpHeaders;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StreamingHtmlContentExtractorTests {

    @TempDir
    Path temporaryDirectory;

    private final StreamingHtmlContentExtractor extractor =
            new StreamingHtmlContentExtractor();

    @Test
    void extractsNormalizedSnippetAndHtmlMetadata()
            throws Exception {

        String html = """
                <!doctype html>
                <html>
                <head>
                    <title>
                        SiteSentinel   Security Report
                    </title>
                    <meta
                        name="description"
                        content="  Continuous website security monitoring.  ">
                    <link
                        rel="alternate canonical"
                        href="  https://example.com/security  ">
                </head>
                <body>
                    <h1>Security monitoring</h1>
                    <p>Completed successfully.</p>
                </body>
                </html>
                """;

        try (CollectedHttpResponse response =
                     createInMemoryResponse(
                             html.getBytes(
                                     StandardCharsets.UTF_8
                             )
                     )) {

            StreamingHtmlContentExtractor.HtmlContent result =
                    extractor.extract(
                            response,
                            StandardCharsets.UTF_8,
                            1000
                    );

            assertEquals(
                    Optional.of(
                            "SiteSentinel Security Report "
                                    + "Security monitoring "
                                    + "Completed successfully."
                    ),
                    result.bodySnippet()
            );

            assertEquals(
                    Optional.of(
                            "SiteSentinel Security Report"
                    ),
                    result.pageTitle()
            );

            assertEquals(
                    Optional.of(
                            "Continuous website security monitoring."
                    ),
                    result.metaDescription()
            );

            assertEquals(
                    Optional.of(
                            "https://example.com/security"
                    ),
                    result.canonicalUrl()
            );
        }
    }

    @Test
    void scriptAndStyleContentAreExcludedFromSnippet()
            throws Exception {

        String html = """
                <html>
                <head>
                    <title>SiteSentinel</title>
                    <style>
                        .hidden {
                            color: red;
                        }
                    </style>
                    <script>
                        window.internalSecret = "not visible";
                    </script>
                </head>
                <body>
                    <h1>Visible security result</h1>
                </body>
                </html>
                """;

        try (CollectedHttpResponse response =
                     createInMemoryResponse(
                             html.getBytes(
                                     StandardCharsets.UTF_8
                             )
                     )) {

            StreamingHtmlContentExtractor.HtmlContent result =
                    extractor.extract(
                            response,
                            StandardCharsets.UTF_8,
                            1000
                    );

            String snippet =
                    result.bodySnippet()
                            .orElseThrow();

            assertEquals(
                    "SiteSentinel Visible security result",
                    snippet
            );

            assertFalse(
                    snippet.contains(
                            "internalSecret"
                    )
            );

            assertFalse(
                    snippet.contains(
                            "color: red"
                    )
            );

            assertFalse(
                    snippet.contains(
                            "not visible"
                    )
            );
        }
    }

    @Test
    void snippetLimitDoesNotStopRemainingHtmlParsing()
            throws Exception {

        String longTitleText =
                "Visible monitoring content "
                        .repeat(500);

        String html =
                "<html>"
                        + "<head>"
                        + "<title>"
                        + longTitleText
                        + "</title>"
                        + "<meta name=\"description\" "
                        + "content=\"Metadata found after long content\">"
                        + "<link rel=\"canonical\" "
                        + "href=\"https://example.com/final\">"
                        + "</head>"
                        + "<body>"
                        + "<p>Tail body content reached.</p>"
                        + "</body>"
                        + "</html>";

        try (CollectedHttpResponse response =
                     createInMemoryResponse(
                             html.getBytes(
                                     StandardCharsets.UTF_8
                             )
                     )) {

            StreamingHtmlContentExtractor.HtmlContent result =
                    extractor.extract(
                            response,
                            StandardCharsets.UTF_8,
                            40
                    );

            String snippet =
                    result.bodySnippet()
                            .orElseThrow();

            assertEquals(
                    40,
                    snippet.length()
            );

            assertEquals(
                    Optional.of(
                            "Metadata found after long content"
                    ),
                    result.metaDescription()
            );

            assertEquals(
                    Optional.of(
                            "https://example.com/final"
                    ),
                    result.canonicalUrl()
            );
        }
    }

    @Test
    void fileBackedHtmlIsFullyParsedWithoutTruncation()
            throws Exception {

        String repeatedContent =
                "<p>Monitored website content</p>"
                        .repeat(10_000);

        String html =
                "<html>"
                        + "<head>"
                        + "<title>Large Customer Website</title>"
                        + "<meta name=\"description\" "
                        + "content=\"Large file metadata\">"
                        + "</head>"
                        + "<body>"
                        + repeatedContent
                        + "<p>Tail content was reached</p>"
                        + "</body>"
                        + "</html>";

        Path responseFile =
                temporaryDirectory.resolve(
                        "large-html-response.tmp"
                );

        Files.write(
                responseFile,
                html.getBytes(
                        StandardCharsets.UTF_8
                )
        );

        CollectedHttpResponse response =
                createFileBackedResponse(
                        responseFile
                );

        StreamingHtmlContentExtractor.HtmlContent result =
                extractor.extract(
                        response,
                        StandardCharsets.UTF_8,
                        400_000
                );

        assertTrue(
                response.isBodyTemporaryFileBacked()
        );

        assertTrue(
                Files.exists(responseFile)
        );

        assertEquals(
                Optional.of(
                        "Large Customer Website"
                ),
                result.pageTitle()
        );

        assertEquals(
                Optional.of(
                        "Large file metadata"
                ),
                result.metaDescription()
        );

        String snippet =
                result.bodySnippet()
                        .orElseThrow();

        assertTrue(
                snippet.startsWith(
                        "Large Customer Website "
                                + "Monitored website content"
                )
        );

        assertTrue(
                snippet.endsWith(
                        "Tail content was reached"
                )
        );

        response.close();

        assertFalse(
                Files.exists(responseFile)
        );
    }

    @Test
    void sourceCharsetIsAppliedDuringHtmlExtraction()
            throws Exception {

        String html = """
                <html>
                <head>
                    <title>Sécurité du site</title>
                    <meta
                        name="description"
                        content="Contrôle terminé avec succès">
                </head>
                <body>
                    <p>café déjà vérifié</p>
                </body>
                </html>
                """;

        try (CollectedHttpResponse response =
                     createInMemoryResponse(
                             html.getBytes(
                                     StandardCharsets.ISO_8859_1
                             )
                     )) {

            StreamingHtmlContentExtractor.HtmlContent result =
                    extractor.extract(
                            response,
                            StandardCharsets.ISO_8859_1,
                            1000
                    );

            assertEquals(
                    Optional.of(
                            "Sécurité du site"
                    ),
                    result.pageTitle()
            );

            assertEquals(
                    Optional.of(
                            "Contrôle terminé avec succès"
                    ),
                    result.metaDescription()
            );

            assertEquals(
                    Optional.of(
                            "Sécurité du site café déjà vérifié"
                    ),
                    result.bodySnippet()
            );
        }
    }

    @Test
    void missingHtmlValuesProduceEmptyOptionals()
            throws Exception {

        String html =
                "<html><head></head><body></body></html>";

        try (CollectedHttpResponse response =
                     createInMemoryResponse(
                             html.getBytes(
                                     StandardCharsets.UTF_8
                             )
                     )) {

            StreamingHtmlContentExtractor.HtmlContent result =
                    extractor.extract(
                            response,
                            StandardCharsets.UTF_8,
                            1000
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
    }

    @Test
    void invalidExtractionArgumentsAreRejected()
            throws Exception {

        assertThrows(
                NullPointerException.class,
                () -> extractor.extract(
                        null,
                        StandardCharsets.UTF_8,
                        1000
                )
        );

        try (CollectedHttpResponse response =
                     createInMemoryResponse(
                             new byte[0]
                     )) {

            assertThrows(
                    NullPointerException.class,
                    () -> extractor.extract(
                            response,
                            null,
                            1000
                    )
            );

            assertThrows(
                    IllegalArgumentException.class,
                    () -> extractor.extract(
                            response,
                            StandardCharsets.UTF_8,
                            0
                    )
            );

            assertThrows(
                    IllegalArgumentException.class,
                    () -> extractor.extract(
                            response,
                            StandardCharsets.UTF_8,
                            -1
                    )
            );
        }
    }

    private CollectedHttpResponse createInMemoryResponse(
            byte[] content
    ) {
        return new CollectedHttpResponse(
                200,
                URI.create(
                        "https://example.com/"
                ),
                emptyHeaders(),
                StoredResponseBody.inMemory(
                        content
                )
        );
    }

    private CollectedHttpResponse createFileBackedResponse(
            Path responseFile
    ) throws Exception {
        return new CollectedHttpResponse(
                200,
                URI.create(
                        "https://example.com/"
                ),
                emptyHeaders(),
                StoredResponseBody.temporaryFile(
                        responseFile
                )
        );
    }

    private HttpHeaders emptyHeaders() {
        return HttpHeaders.of(
                Map.of(),
                (headerName, headerValue) -> true
        );
    }
}