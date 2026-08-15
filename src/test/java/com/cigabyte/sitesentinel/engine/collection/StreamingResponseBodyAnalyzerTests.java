package com.cigabyte.sitesentinel.engine.collection;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.net.URI;
import java.net.http.HttpHeaders;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StreamingResponseBodyAnalyzerTests {

    @TempDir
    Path temporaryDirectory;

    private final StreamingResponseBodyAnalyzer analyzer =
            new StreamingResponseBodyAnalyzer(
                    new StreamingResponseBodyFingerprintCalculator(),
                    new StreamingHtmlContentExtractor()
            );

    @Test
    void htmlResponseProducesFingerprintSnippetAndMetadata()
            throws Exception {

        String html = """
                <!doctype html>
                <html>
                <head>
                    <title>SiteSentinel</title>
                    <meta
                        name="description"
                        content="Continuous website monitoring">
                    <link
                        rel="canonical"
                        href="https://example.com/security">
                </head>
                <body>
                    <h1>Security monitoring completed.</h1>
                </body>
                </html>
                """;

        byte[] responseBytes =
                html.getBytes(
                        StandardCharsets.UTF_8
                );

        try (CollectedHttpResponse response =
                     createInMemoryResponse(
                             responseBytes
                     )) {

            ResponseBodyAnalysisResult result =
                    analyzer.analyze(
                            response,
                            StandardCharsets.UTF_8,
                            "TEXT/HTML; charset=UTF-8",
                            1000
                    );

            assertEquals(
                    responseBytes.length,
                    result.byteLength()
            );

            assertEquals(
                    html.length(),
                    result.characterLength()
            );

            assertEquals(
                    sha256Utf8(html),
                    result.sha256()
            );

            assertEquals(
                    Optional.of(
                            "SiteSentinel "
                                    + "Security monitoring completed."
                    ),
                    result.bodySnippet()
            );

            assertEquals(
                    Optional.of("SiteSentinel"),
                    result.pageTitle()
            );

            assertEquals(
                    Optional.of(
                            "Continuous website monitoring"
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
    void nonHtmlResponsePreservesFingerprintAndSnippetButSuppressesMetadata()
            throws Exception {

        String content = """
                User-agent: *
                Disallow: /private
                Sitemap: https://example.com/sitemap.xml
                """;

        byte[] responseBytes =
                content.getBytes(
                        StandardCharsets.UTF_8
                );

        try (CollectedHttpResponse response =
                     createInMemoryResponse(
                             responseBytes
                     )) {

            ResponseBodyAnalysisResult result =
                    analyzer.analyze(
                            response,
                            StandardCharsets.UTF_8,
                            "text/plain; charset=UTF-8",
                            1000
                    );

            assertEquals(
                    responseBytes.length,
                    result.byteLength()
            );

            assertEquals(
                    content.length(),
                    result.characterLength()
            );

            assertEquals(
                    sha256Utf8(content),
                    result.sha256()
            );

            assertEquals(
                    Optional.of(
                            "User-agent: * "
                                    + "Disallow: /private "
                                    + "Sitemap: "
                                    + "https://example.com/sitemap.xml"
                    ),
                    result.bodySnippet()
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
    void missingOrBlankContentTypeSuppressesHtmlMetadata()
            throws Exception {

        String html = """
                <html>
                <head>
                    <title>Hidden classification title</title>
                    <meta
                        name="description"
                        content="Hidden classification description">
                    <link
                        rel="canonical"
                        href="https://example.com/hidden">
                </head>
                <body>
                    Visible content
                </body>
                </html>
                """;

        try (CollectedHttpResponse response =
                     createInMemoryResponse(
                             html.getBytes(
                                     StandardCharsets.UTF_8
                             )
                     )) {

            ResponseBodyAnalysisResult missingTypeResult =
                    analyzer.analyze(
                            response,
                            StandardCharsets.UTF_8,
                            null,
                            1000
                    );

            assertEquals(
                    Optional.of(
                            "Hidden classification title "
                                    + "Visible content"
                    ),
                    missingTypeResult.bodySnippet()
            );

            assertTrue(
                    missingTypeResult
                            .pageTitle()
                            .isEmpty()
            );

            assertTrue(
                    missingTypeResult
                            .metaDescription()
                            .isEmpty()
            );

            assertTrue(
                    missingTypeResult
                            .canonicalUrl()
                            .isEmpty()
            );

            ResponseBodyAnalysisResult blankTypeResult =
                    analyzer.analyze(
                            response,
                            StandardCharsets.UTF_8,
                            "   ",
                            1000
                    );

            assertTrue(
                    blankTypeResult
                            .pageTitle()
                            .isEmpty()
            );

            assertTrue(
                    blankTypeResult
                            .metaDescription()
                            .isEmpty()
            );

            assertTrue(
                    blankTypeResult
                            .canonicalUrl()
                            .isEmpty()
            );
        }
    }

    @Test
    void fileBackedResponseIsFullyAnalyzedAndOwnershipIsPreserved()
            throws Exception {

        String repeatedBody =
                "<p>Customer website monitored content</p>"
                        .repeat(10_000);

        String html =
                "<html>"
                        + "<head>"
                        + "<title>Large Customer Website</title>"
                        + "<meta name=\"description\" "
                        + "content=\"Large response metadata\">"
                        + "</head>"
                        + "<body>"
                        + repeatedBody
                        + "</body>"
                        + "</html>";

        byte[] responseBytes =
                html.getBytes(
                        StandardCharsets.UTF_8
                );

        Path responseFile =
                temporaryDirectory.resolve(
                        "analyzer-large-response.tmp"
                );

        Files.write(
                responseFile,
                responseBytes
        );

        CollectedHttpResponse response =
                createFileBackedResponse(
                        responseFile
                );

        try {
            ResponseBodyAnalysisResult result =
                    analyzer.analyze(
                            response,
                            StandardCharsets.UTF_8,
                            "text/html",
                            200
                    );

            assertTrue(
                    response
                            .isBodyTemporaryFileBacked()
            );

            assertTrue(
                    Files.exists(responseFile)
            );

            assertEquals(
                    responseBytes.length,
                    result.byteLength()
            );

            assertEquals(
                    html.length(),
                    result.characterLength()
            );

            assertEquals(
                    sha256Utf8(html),
                    result.sha256()
            );

            assertEquals(
                    Optional.of(
                            "Large Customer Website"
                    ),
                    result.pageTitle()
            );

            assertEquals(
                    Optional.of(
                            "Large response metadata"
                    ),
                    result.metaDescription()
            );

            assertTrue(
                    result.bodySnippet()
                            .orElseThrow()
                            .startsWith(
                                    "Large Customer Website "
                                            + "Customer website "
                                            + "monitored content"
                            )
            );

            assertEquals(
                    200,
                    result.bodySnippet()
                            .orElseThrow()
                            .length()
            );
        } finally {
            response.close();
        }

        assertFalse(
                Files.exists(responseFile)
        );
    }

    @Test
    void invalidAnalysisArgumentsAreRejected()
            throws Exception {

        assertThrows(
                NullPointerException.class,
                () -> analyzer.analyze(
                        null,
                        StandardCharsets.UTF_8,
                        "text/html",
                        1000
                )
        );

        try (CollectedHttpResponse response =
                     createInMemoryResponse(
                             new byte[0]
                     )) {

            assertThrows(
                    NullPointerException.class,
                    () -> analyzer.analyze(
                            response,
                            null,
                            "text/html",
                            1000
                    )
            );

            assertThrows(
                    IllegalArgumentException.class,
                    () -> analyzer.analyze(
                            response,
                            StandardCharsets.UTF_8,
                            "text/html",
                            0
                    )
            );

            assertThrows(
                    IllegalArgumentException.class,
                    () -> analyzer.analyze(
                            response,
                            StandardCharsets.UTF_8,
                            "text/html",
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

    private String sha256Utf8(
            String content
    ) throws Exception {

        MessageDigest messageDigest =
                MessageDigest.getInstance(
                        "SHA-256"
                );

        byte[] hash =
                messageDigest.digest(
                        content.getBytes(
                                StandardCharsets.UTF_8
                        )
                );

        return HexFormat.of()
                .formatHex(hash);
    }
}