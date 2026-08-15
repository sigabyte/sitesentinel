package com.cigabyte.sitesentinel.engine.collection;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.InputStream;
import java.io.Reader;
import java.net.URI;
import java.net.http.HttpHeaders;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CollectedHttpResponseTests {

    @TempDir
    Path temporaryDirectory;

    @Test
    void exposesMetadataAndInMemoryBody()
            throws Exception {

        byte[] expectedContent =
                "SiteSentinel response"
                        .getBytes(
                                StandardCharsets.UTF_8
                        );

        URI responseUri =
                URI.create(
                        "https://example.com/final"
                );

        HttpHeaders headers =
                createHeaders();

        try (CollectedHttpResponse response =
                     new CollectedHttpResponse(
                             200,
                             responseUri,
                             headers,
                             StoredResponseBody.inMemory(
                                     expectedContent
                             )
                     )) {

            assertEquals(
                    200,
                    response.getStatusCode()
            );

            assertEquals(
                    responseUri,
                    response.getUri()
            );

            assertEquals(
                    "text/html; charset=UTF-8",
                    response.getHeaders()
                            .firstValue("content-type")
                            .orElseThrow()
            );

            assertEquals(
                    expectedContent.length,
                    response.getBodyByteLength()
            );

            assertTrue(
                    response.isBodyInMemory()
            );

            assertFalse(
                    response
                            .isBodyTemporaryFileBacked()
            );

            try (InputStream inputStream =
                         response.openBodyInputStream()) {

                assertArrayEquals(
                        expectedContent,
                        inputStream.readAllBytes()
                );
            }
        }
    }

    @Test
    void bodyReaderUsesProvidedCharset()
            throws Exception {

        String expectedContent =
                "SiteSentinel güvenli response";

        try (CollectedHttpResponse response =
                     new CollectedHttpResponse(
                             200,
                             URI.create(
                                     "https://example.com/"
                             ),
                             createHeaders(),
                             StoredResponseBody.inMemory(
                                     expectedContent.getBytes(
                                             StandardCharsets.UTF_8
                                     )
                             )
                     );
             Reader reader =
                     response.openBodyReader(
                             StandardCharsets.UTF_8
                     )) {

            assertEquals(
                    expectedContent,
                    readFully(reader)
            );
        }
    }

    @Test
    void closingResponseDeletesTemporaryBodyFile()
            throws Exception {

        byte[] expectedContent =
                "Large response content"
                        .getBytes(
                                StandardCharsets.UTF_8
                        );

        Path responseFile =
                temporaryDirectory.resolve(
                        "collected-response.tmp"
                );

        Files.write(
                responseFile,
                expectedContent
        );

        CollectedHttpResponse response =
                new CollectedHttpResponse(
                        200,
                        URI.create(
                                "https://example.com/"
                        ),
                        createHeaders(),
                        StoredResponseBody.temporaryFile(
                                responseFile
                        )
                );

        assertTrue(
                response
                        .isBodyTemporaryFileBacked()
        );

        assertFalse(
                response.isBodyInMemory()
        );

        assertTrue(
                Files.exists(responseFile)
        );

        try (InputStream inputStream =
                     response.openBodyInputStream()) {

            assertArrayEquals(
                    expectedContent,
                    inputStream.readAllBytes()
            );
        }

        response.close();

        assertFalse(
                Files.exists(responseFile)
        );
    }

    @Test
    void closedResponseRejectsBodyAccessAndCloseIsIdempotent()
            throws Exception {

        CollectedHttpResponse response =
                new CollectedHttpResponse(
                        204,
                        URI.create(
                                "https://example.com/"
                        ),
                        createHeaders(),
                        StoredResponseBody.inMemory(
                                new byte[0]
                        )
                );

        response.close();

        assertThrows(
                IllegalStateException.class,
                response::getBodyByteLength
        );

        assertThrows(
                IllegalStateException.class,
                response::isBodyInMemory
        );

        assertThrows(
                IllegalStateException.class,
                response::isBodyTemporaryFileBacked
        );

        assertThrows(
                IllegalStateException.class,
                response::openBodyInputStream
        );

        assertThrows(
                IllegalStateException.class,
                () -> response.openBodyReader(
                        StandardCharsets.UTF_8
                )
        );

        response.close();
    }

    @Test
    void constructorRejectsInvalidArguments() {

        URI validUri =
                URI.create(
                        "https://example.com/"
                );

        HttpHeaders validHeaders =
                createHeaders();

        assertThrows(
                IllegalArgumentException.class,
                () -> new CollectedHttpResponse(
                        99,
                        validUri,
                        validHeaders,
                        StoredResponseBody.inMemory(
                                new byte[0]
                        )
                )
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> new CollectedHttpResponse(
                        600,
                        validUri,
                        validHeaders,
                        StoredResponseBody.inMemory(
                                new byte[0]
                        )
                )
        );

        assertThrows(
                NullPointerException.class,
                () -> new CollectedHttpResponse(
                        200,
                        null,
                        validHeaders,
                        StoredResponseBody.inMemory(
                                new byte[0]
                        )
                )
        );

        assertThrows(
                NullPointerException.class,
                () -> new CollectedHttpResponse(
                        200,
                        validUri,
                        null,
                        StoredResponseBody.inMemory(
                                new byte[0]
                        )
                )
        );

        assertThrows(
                NullPointerException.class,
                () -> new CollectedHttpResponse(
                        200,
                        validUri,
                        validHeaders,
                        null
                )
        );
    }

    private HttpHeaders createHeaders() {
        return HttpHeaders.of(
                Map.of(
                        "content-type",
                        List.of(
                                "text/html; charset=UTF-8"
                        )
                ),
                (headerName, headerValue) -> true
        );
    }

    private String readFully(
            Reader reader
    ) throws Exception {

        StringBuilder content =
                new StringBuilder();

        char[] buffer =
                new char[256];

        int readCount;

        while ((readCount =
                reader.read(buffer)) != -1) {

            content.append(
                    buffer,
                    0,
                    readCount
            );
        }

        return content.toString();
    }
}