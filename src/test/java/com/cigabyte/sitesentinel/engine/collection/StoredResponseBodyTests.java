package com.cigabyte.sitesentinel.engine.collection;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StoredResponseBodyTests {

    @TempDir
    Path temporaryDirectory;

    @Test
    void inMemoryProtectsContentAndProvidesRepeatableStreams()
            throws Exception {

        byte[] originalContent =
                "SiteSentinel response"
                        .getBytes(
                                StandardCharsets.UTF_8
                        );

        try (StoredResponseBody responseBody =
                     StoredResponseBody.inMemory(
                             originalContent
                     )) {

            originalContent[0] = 'X';

            assertTrue(
                    responseBody.isInMemory()
            );

            assertFalse(
                    responseBody.isTemporaryFileBacked()
            );

            assertEquals(
                    "SiteSentinel response"
                            .getBytes(
                                    StandardCharsets.UTF_8
                            )
                            .length,
                    responseBody.getByteLength()
            );

            try (InputStream firstStream =
                         responseBody.openInputStream();
                 InputStream secondStream =
                         responseBody.openInputStream()) {

                byte[] expectedContent =
                        "SiteSentinel response"
                                .getBytes(
                                        StandardCharsets.UTF_8
                                );

                assertArrayEquals(
                        expectedContent,
                        firstStream.readAllBytes()
                );

                assertArrayEquals(
                        expectedContent,
                        secondStream.readAllBytes()
                );
            }
        }
    }

    @Test
    void openReaderDecodesContentUsingProvidedCharset()
            throws Exception {

        String expectedContent =
                "SiteSentinel güvenli tarama";

        try (StoredResponseBody responseBody =
                     StoredResponseBody.inMemory(
                             expectedContent.getBytes(
                                     StandardCharsets.UTF_8
                             )
                     );
             Reader reader =
                     responseBody.openReader(
                             StandardCharsets.UTF_8
                     )) {

            String actualContent =
                    readFully(reader);

            assertEquals(
                    expectedContent,
                    actualContent
            );
        }
    }

    @Test
    void temporaryFileProvidesStreamAccessAndMetadata()
            throws Exception {

        byte[] expectedContent =
                "Large response body"
                        .getBytes(
                                StandardCharsets.UTF_8
                        );

        Path responseFile =
                temporaryDirectory.resolve(
                        "response-body.tmp"
                );

        Files.write(
                responseFile,
                expectedContent
        );

        try (StoredResponseBody responseBody =
                     StoredResponseBody.temporaryFile(
                             responseFile
                     )) {

            assertFalse(
                    responseBody.isInMemory()
            );

            assertTrue(
                    responseBody.isTemporaryFileBacked()
            );

            assertEquals(
                    expectedContent.length,
                    responseBody.getByteLength()
            );

            try (InputStream inputStream =
                         responseBody.openInputStream()) {

                assertArrayEquals(
                        expectedContent,
                        inputStream.readAllBytes()
                );
            }

            assertTrue(
                    Files.exists(responseFile)
            );
        }

        assertFalse(
                Files.exists(responseFile)
        );
    }

    @Test
    void closeRejectsFurtherBodyAccess()
            throws Exception {

        StoredResponseBody responseBody =
                StoredResponseBody.inMemory(
                        "response"
                                .getBytes(
                                        StandardCharsets.UTF_8
                                )
                );

        responseBody.close();

        assertThrows(
                IllegalStateException.class,
                responseBody::openInputStream
        );

        assertThrows(
                IllegalStateException.class,
                () -> responseBody.openReader(
                        StandardCharsets.UTF_8
                )
        );

        responseBody.close();
    }

    @Test
    void temporaryFileRejectsMissingOrNonRegularPath()
            throws Exception {

        Path missingFile =
                temporaryDirectory.resolve(
                        "missing-response.tmp"
                );

        assertThrows(
                IllegalArgumentException.class,
                () -> StoredResponseBody.temporaryFile(
                        missingFile
                )
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> StoredResponseBody.temporaryFile(
                        temporaryDirectory
                )
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

        while ((readCount = reader.read(buffer)) != -1) {
            content.append(
                    buffer,
                    0,
                    readCount
            );
        }

        return content.toString();
    }
}