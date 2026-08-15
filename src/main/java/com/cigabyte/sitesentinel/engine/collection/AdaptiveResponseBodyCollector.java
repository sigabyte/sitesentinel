package com.cigabyte.sitesentinel.engine.collection;

import com.cigabyte.sitesentinel.scanner.ScannerProperties;
import org.springframework.stereotype.Component;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

@Component
public class AdaptiveResponseBodyCollector {

    private static final int READ_BUFFER_SIZE = 8 * 1024;

    private static final String TEMPORARY_FILE_PREFIX =
            "sitesentinel-response-body-";

    private static final String TEMPORARY_FILE_SUFFIX =
            ".tmp";

    private final ScannerProperties scannerProperties;

    public AdaptiveResponseBodyCollector(
            ScannerProperties scannerProperties
    ) {
        this.scannerProperties = scannerProperties;
    }

    public StoredResponseBody collect(
            InputStream responseStream
    ) throws IOException {
        Objects.requireNonNull(
                responseStream,
                "HTTP response body stream is required."
        );

        long inMemoryThreshold =
                scannerProperties
                        .getInMemoryBodyThresholdBytes();

        Path temporaryFile = null;
        OutputStream temporaryFileOutput = null;

        try (InputStream inputStream = responseStream;
             ByteArrayOutputStream inMemoryOutput =
                     new ByteArrayOutputStream(
                             initialCapacity(
                                     inMemoryThreshold
                             )
                     )) {

            byte[] buffer =
                    new byte[READ_BUFFER_SIZE];

            long totalBytesRead = 0;
            int bytesRead;

            while ((bytesRead =
                    inputStream.read(buffer)) != -1) {

                long nextTotalBytesRead =
                        Math.addExact(
                                totalBytesRead,
                                bytesRead
                        );

                if (temporaryFileOutput == null
                        && nextTotalBytesRead
                        > inMemoryThreshold) {

                    temporaryFile =
                            createTemporaryFile();

                    temporaryFileOutput =
                            new BufferedOutputStream(
                                    Files.newOutputStream(
                                            temporaryFile
                                    )
                            );

                    inMemoryOutput.writeTo(
                            temporaryFileOutput
                    );
                }

                if (temporaryFileOutput == null) {
                    inMemoryOutput.write(
                            buffer,
                            0,
                            bytesRead
                    );
                } else {
                    temporaryFileOutput.write(
                            buffer,
                            0,
                            bytesRead
                    );
                }

                totalBytesRead =
                        nextTotalBytesRead;
            }

            if (temporaryFileOutput == null) {
                return StoredResponseBody.inMemory(
                        inMemoryOutput.toByteArray()
                );
            }

            temporaryFileOutput.close();
            temporaryFileOutput = null;

            StoredResponseBody storedResponseBody =
                    StoredResponseBody.temporaryFile(
                            temporaryFile
                    );

            temporaryFile = null;

            return storedResponseBody;
        } finally {
            closeQuietly(temporaryFileOutput);
            deleteQuietly(temporaryFile);
        }
    }

    private Path createTemporaryFile()
            throws IOException {

        if (!scannerProperties
                .hasConfiguredTemporaryDirectory()) {

            return Files.createTempFile(
                    TEMPORARY_FILE_PREFIX,
                    TEMPORARY_FILE_SUFFIX
            );
        }

        Path temporaryDirectory =
                Path.of(
                                scannerProperties
                                        .getTemporaryDirectory()
                        )
                        .toAbsolutePath()
                        .normalize();

        Files.createDirectories(
                temporaryDirectory
        );

        return Files.createTempFile(
                temporaryDirectory,
                TEMPORARY_FILE_PREFIX,
                TEMPORARY_FILE_SUFFIX
        );
    }

    private int initialCapacity(
            long inMemoryThreshold
    ) {
        return (int) Math.max(
                32,
                Math.min(
                        READ_BUFFER_SIZE,
                        inMemoryThreshold
                )
        );
    }

    private void closeQuietly(
            OutputStream outputStream
    ) {
        if (outputStream == null) {
            return;
        }

        try {
            outputStream.close();
        } catch (IOException ignored) {
            // The original collection failure remains primary.
        }
    }

    private void deleteQuietly(
            Path temporaryFile
    ) {
        if (temporaryFile == null) {
            return;
        }

        try {
            Files.deleteIfExists(
                    temporaryFile
            );
        } catch (IOException ignored) {
            // Best-effort cleanup after an incomplete collection.
        }
    }
}