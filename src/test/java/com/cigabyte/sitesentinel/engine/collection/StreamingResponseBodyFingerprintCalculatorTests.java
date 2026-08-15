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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StreamingResponseBodyFingerprintCalculatorTests {

    @TempDir
    Path temporaryDirectory;

    private final StreamingResponseBodyFingerprintCalculator
            calculator =
            new StreamingResponseBodyFingerprintCalculator();

    @Test
    void calculatesFingerprintForInMemoryUtf8Body()
            throws Exception {

        String content =
                "SiteSentinel response body";

        try (CollectedHttpResponse response =
                     createInMemoryResponse(
                             content.getBytes(
                                     StandardCharsets.UTF_8
                             )
                     )) {

            StreamingResponseBodyFingerprintCalculator
                    .Fingerprint fingerprint =
                    calculator.calculate(
                            response,
                            StandardCharsets.UTF_8
                    );

            assertEquals(
                    content.length(),
                    fingerprint.characterLength()
            );

            assertEquals(
                    sha256Utf8(content),
                    fingerprint.sha256()
            );

            assertTrue(
                    response.isBodyInMemory()
            );

            assertEquals(
                    content.getBytes(
                            StandardCharsets.UTF_8
                    ).length,
                    response.getBodyByteLength()
            );
        }
    }

    @Test
    void unicodeContentPreservesExistingStringLengthAndHashSemantics()
            throws Exception {

        String content =
                "SiteSentinel güvenli tarama 中文 🔒";

        try (CollectedHttpResponse response =
                     createInMemoryResponse(
                             content.getBytes(
                                     StandardCharsets.UTF_8
                             )
                     )) {

            StreamingResponseBodyFingerprintCalculator
                    .Fingerprint fingerprint =
                    calculator.calculate(
                            response,
                            StandardCharsets.UTF_8
                    );

            assertEquals(
                    content.length(),
                    fingerprint.characterLength()
            );

            assertEquals(
                    sha256Utf8(content),
                    fingerprint.sha256()
            );
        }
    }

    @Test
    void sourceCharsetIsAppliedBeforeUtf8Fingerprinting()
            throws Exception {

        String content =
                "café déjà vu";

        byte[] sourceBytes =
                content.getBytes(
                        StandardCharsets.ISO_8859_1
                );

        try (CollectedHttpResponse response =
                     createInMemoryResponse(
                             sourceBytes
                     )) {

            StreamingResponseBodyFingerprintCalculator
                    .Fingerprint fingerprint =
                    calculator.calculate(
                            response,
                            StandardCharsets.ISO_8859_1
                    );

            assertEquals(
                    content.length(),
                    fingerprint.characterLength()
            );

            assertEquals(
                    sha256Utf8(content),
                    fingerprint.sha256()
            );
        }
    }

    @Test
    void fileBackedResponseIsFullyFingerprintedAndNotTruncated()
            throws Exception {

        String content =
                "SiteSentinel large response line\n"
                        .repeat(10_000);

        Path responseFile =
                temporaryDirectory.resolve(
                        "large-response-body.tmp"
                );

        Files.write(
                responseFile,
                content.getBytes(
                        StandardCharsets.UTF_8
                )
        );

        CollectedHttpResponse response =
                createFileBackedResponse(
                        responseFile
                );

        StreamingResponseBodyFingerprintCalculator
                .Fingerprint fingerprint =
                calculator.calculate(
                        response,
                        StandardCharsets.UTF_8
                );

        assertTrue(
                response.isBodyTemporaryFileBacked()
        );

        assertEquals(
                content.length(),
                fingerprint.characterLength()
        );

        assertEquals(
                sha256Utf8(content),
                fingerprint.sha256()
        );

        assertTrue(
                Files.exists(responseFile)
        );

        response.close();

        assertFalse(
                Files.exists(responseFile)
        );
    }

    @Test
    void emptyResponseProducesEmptyContentFingerprint()
            throws Exception {

        try (CollectedHttpResponse response =
                     createInMemoryResponse(
                             new byte[0]
                     )) {

            StreamingResponseBodyFingerprintCalculator
                    .Fingerprint fingerprint =
                    calculator.calculate(
                            response,
                            StandardCharsets.UTF_8
                    );

            assertEquals(
                    0,
                    fingerprint.characterLength()
            );

            assertEquals(
                    sha256Utf8(""),
                    fingerprint.sha256()
            );
        }
    }

    @Test
    void nullCalculationArgumentsAreRejected()
            throws Exception {

        assertThrows(
                NullPointerException.class,
                () -> calculator.calculate(
                        null,
                        StandardCharsets.UTF_8
                )
        );

        try (CollectedHttpResponse response =
                     createInMemoryResponse(
                             new byte[0]
                     )) {

            assertThrows(
                    NullPointerException.class,
                    () -> calculator.calculate(
                            response,
                            null
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