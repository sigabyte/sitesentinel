package com.cigabyte.sitesentinel.engine.collection;

import com.cigabyte.sitesentinel.scanner.ScannerProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AdaptiveResponseBodyCollectorTests {

    private static final int IN_MEMORY_THRESHOLD_BYTES =
            64 * 1024;

    @TempDir
    Path temporaryDirectory;

    @Test
    void emptyResponseRemainsInMemory()
            throws Exception {

        AdaptiveResponseBodyCollector collector =
                createCollector();

        try (StoredResponseBody responseBody =
                     collector.collect(
                             new ByteArrayInputStream(
                                     new byte[0]
                             )
                     )) {

            assertTrue(
                    responseBody.isInMemory()
            );

            assertFalse(
                    responseBody.isTemporaryFileBacked()
            );

            assertEquals(
                    0,
                    responseBody.getByteLength()
            );

            try (InputStream inputStream =
                         responseBody.openInputStream()) {

                assertArrayEquals(
                        new byte[0],
                        inputStream.readAllBytes()
                );
            }
        }
    }

    @Test
    void responseAtThresholdRemainsInMemory()
            throws Exception {

        byte[] expectedContent =
                createPayload(
                        IN_MEMORY_THRESHOLD_BYTES
                );

        AdaptiveResponseBodyCollector collector =
                createCollector();

        try (StoredResponseBody responseBody =
                     collector.collect(
                             new ByteArrayInputStream(
                                     expectedContent
                             )
                     )) {

            assertTrue(
                    responseBody.isInMemory()
            );

            assertFalse(
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
        }

        assertEquals(
                0,
                countFilesInTemporaryDirectory()
        );
    }

    @Test
    void responseAboveThresholdSpillsToFileAndPreservesAllBytes()
            throws Exception {

        byte[] expectedContent =
                createPayload(
                        IN_MEMORY_THRESHOLD_BYTES + 1
                );

        AdaptiveResponseBodyCollector collector =
                createCollector();

        try (StoredResponseBody responseBody =
                     collector.collect(
                             new ByteArrayInputStream(
                                     expectedContent
                             )
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

            assertEquals(
                    1,
                    countFilesInTemporaryDirectory()
            );

            try (InputStream inputStream =
                         responseBody.openInputStream()) {

                assertArrayEquals(
                        expectedContent,
                        inputStream.readAllBytes()
                );
            }
        }

        assertEquals(
                0,
                countFilesInTemporaryDirectory()
        );
    }

    @Test
    void responseStreamIsClosedAfterSuccessfulCollection()
            throws Exception {

        TrackingInputStream responseStream =
                new TrackingInputStream(
                        createPayload(1024)
                );

        AdaptiveResponseBodyCollector collector =
                createCollector();

        try (StoredResponseBody responseBody =
                     collector.collect(
                             responseStream
                     )) {

            assertTrue(
                    responseBody.isInMemory()
            );
        }

        assertTrue(
                responseStream.isClosed()
        );
    }

    @Test
    void readFailureClosesStreamAndDeletesIncompleteTemporaryFile()
            throws Exception {

        byte[] responseContent =
                createPayload(
                        IN_MEMORY_THRESHOLD_BYTES
                                + 1024
                );

        FailingInputStream responseStream =
                new FailingInputStream(
                        responseContent,
                        IN_MEMORY_THRESHOLD_BYTES + 1
                );

        AdaptiveResponseBodyCollector collector =
                createCollector();

        IOException exception =
                assertThrows(
                        IOException.class,
                        () -> collector.collect(
                                responseStream
                        )
                );

        assertEquals(
                "Simulated response stream failure.",
                exception.getMessage()
        );

        assertTrue(
                responseStream.isClosed()
        );

        assertEquals(
                0,
                countFilesInTemporaryDirectory()
        );
    }

    @Test
    void responseLargerThanThresholdIsNotTruncated()
            throws Exception {

        byte[] expectedContent =
                createPayload(
                        IN_MEMORY_THRESHOLD_BYTES
                                * 3
                                + 417
                );

        AdaptiveResponseBodyCollector collector =
                createCollector();

        try (StoredResponseBody responseBody =
                     collector.collect(
                             new ByteArrayInputStream(
                                     expectedContent
                             )
                     )) {

            assertTrue(
                    responseBody.isTemporaryFileBacked()
            );

            assertEquals(
                    expectedContent.length,
                    responseBody.getByteLength()
            );

            try (InputStream inputStream =
                         responseBody.openInputStream()) {

                byte[] actualContent =
                        inputStream.readAllBytes();

                assertEquals(
                        expectedContent.length,
                        actualContent.length
                );

                assertArrayEquals(
                        expectedContent,
                        actualContent
                );
            }
        }
    }

    private AdaptiveResponseBodyCollector createCollector() {
        ScannerProperties scannerProperties =
                new ScannerProperties();

        scannerProperties
                .setInMemoryBodyThresholdBytes(
                        IN_MEMORY_THRESHOLD_BYTES
                );

        scannerProperties.setTemporaryDirectory(
                temporaryDirectory.toString()
        );

        return new AdaptiveResponseBodyCollector(
                scannerProperties
        );
    }

    private byte[] createPayload(
            int byteLength
    ) {
        byte[] payload =
                new byte[byteLength];

        for (int index = 0;
             index < payload.length;
             index++) {

            payload[index] =
                    (byte) (index % 251);
        }

        return payload;
    }

    private long countFilesInTemporaryDirectory()
            throws IOException {

        try (Stream<Path> files =
                     Files.list(
                             temporaryDirectory
                     )) {

            return files
                    .filter(Files::isRegularFile)
                    .count();
        }
    }

    private static final class TrackingInputStream
            extends ByteArrayInputStream {

        private boolean closed;

        private TrackingInputStream(
                byte[] content
        ) {
            super(content);
        }

        @Override
        public void close()
                throws IOException {
            closed = true;
            super.close();
        }

        private boolean isClosed() {
            return closed;
        }
    }

    private static final class FailingInputStream
            extends InputStream {

        private final byte[] content;
        private final int failAfterBytes;

        private int position;
        private boolean closed;

        private FailingInputStream(
                byte[] content,
                int failAfterBytes
        ) {
            this.content = content;
            this.failAfterBytes = failAfterBytes;
        }

        @Override
        public int read()
                throws IOException {

            if (position >= failAfterBytes) {
                throw new IOException(
                        "Simulated response stream failure."
                );
            }

            if (position >= content.length) {
                return -1;
            }

            return content[position++] & 0xff;
        }

        @Override
        public int read(
                byte[] buffer,
                int offset,
                int length
        ) throws IOException {

            if (length == 0) {
                return 0;
            }

            if (position >= failAfterBytes) {
                throw new IOException(
                        "Simulated response stream failure."
                );
            }

            if (position >= content.length) {
                return -1;
            }

            int readableUntilFailure =
                    failAfterBytes - position;

            int readableUntilEnd =
                    content.length - position;

            int bytesToRead =
                    Math.min(
                            length,
                            Math.min(
                                    readableUntilFailure,
                                    readableUntilEnd
                            )
                    );

            System.arraycopy(
                    content,
                    position,
                    buffer,
                    offset,
                    bytesToRead
            );

            position += bytesToRead;

            return bytesToRead;
        }

        @Override
        public void close() {
            closed = true;
        }

        private boolean isClosed() {
            return closed;
        }
    }
}